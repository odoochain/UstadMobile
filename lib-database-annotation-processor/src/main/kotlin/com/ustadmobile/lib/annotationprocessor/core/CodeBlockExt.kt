package com.ustadmobile.lib.annotationprocessor.core

import android.arch.persistence.room.ColumnInfo
import com.squareup.kotlinpoet.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

/**
 * Generate a delegation style function call, e.g.
 * varName.callMethod(param1, param2, param3)
 *
 * @param varName the variable name for the object that has the desired function
 * @param funSpec the function spec that we are generating a delegated call for
 */
fun CodeBlock.Builder.addDelegateFunctionCall(varName: String, funSpec: FunSpec) : CodeBlock.Builder {
    return add("$varName.${funSpec.name}(")
            .add(funSpec.parameters.filter { !isContinuationParam(it.type)}.joinToString { it.name })
            .add(")")
}

/**
 * Add a section to this CodeBlock that will declare a variable for the clientId and get it
 * from the header.
 *
 * e.g.
 * val clientIdVarName = call.request.header("X-nid")?.toInt() ?: 0
 *
 * @param varName the varname to create in the CodeBlock
 * @param serverType SERVER_TYPE_KTOR or SERVER_TYPE_NANOHTTPD
 *
 */
fun CodeBlock.Builder.addGetClientIdHeader(varName: String, serverType: Int) : CodeBlock.Builder {
    takeIf { serverType == DbProcessorKtorServer.SERVER_TYPE_KTOR }
            ?.add("val $varName = %M.request.%M(%S)?.toInt() ?: 0\n",
                    DbProcessorKtorServer.CALL_MEMBER,
                    MemberName("io.ktor.request","header"),
                    "x-nid")
    takeIf { serverType == DbProcessorKtorServer.SERVER_TYPE_NANOHTTPD }
            ?.add("val $varName = _session.headers.get(%S)?.toInt() ?: 0\n",
                "x-nid")

    return this
}

fun CodeBlock.Builder.beginIfNotNullOrEmptyControlFlow(varName: String, isList: Boolean) : CodeBlock.Builder{
    if(isList) {
        beginControlFlow("if(!$varName.isEmpty())")
    }else {
        beginControlFlow("if($varName != null)")
    }

    return this
}

/**
 * Generate insert statements that will insert the TableSyncStatus entities required for each syncable
 * entity on the database in place.
 *
 * e.g.
 * _stmt.executeUpdate("INSERT INTO TableSyncstatus(tsTableId, tsLastChanged, tsLastSynced) VALUES (42, ${systemTimeInMillis(), 0)")")
 * _stmt.executeUpdate("INSERT INTO TableSyncstatus(tsTableId, tsLastChanged, tsLastSynced) VALUES (43, ${systemTimeInMillis(), 0)")")
 * ...
 *
 * @param dbType TypeElement of the database itself
 * @param execSqlFunName the name of the function that must be called to execute SQL
 * @param processingEnv the processing environment
 *
 * @return the same CodeBlock.Builder
 */
fun CodeBlock.Builder.addInsertTableSyncStatuses(dbType: TypeElement,
                                               execSqlFunName: String = "_stmt.executeUpdate",
                                               processingEnv: ProcessingEnvironment) : CodeBlock.Builder{

    syncableEntityTypesOnDb(dbType, processingEnv).forEach {
        val syncableEntityInfo = SyncableEntityInfo(it.asClassName(), processingEnv)
        addInsertTableSyncStatus(syncableEntityInfo, execSqlFunName, processingEnv)
    }

    return this
}

fun CodeBlock.Builder.addInsertTableSyncStatus(syncableEntityInfo: SyncableEntityInfo,
                                               execSqlFunName: String = "_stmt.executeUpdate",
                                               processingEnv: ProcessingEnvironment): CodeBlock.Builder {
    add("$execSqlFunName(\"INSERT·INTO·TableSyncStatus(tsTableId,·tsLastChanged,·tsLastSynced)·" +
            "VALUES(${syncableEntityInfo.tableId},·\${%M()},·0)\")\n",
            MemberName("com.ustadmobile.door.util", "systemTimeInMillis"))
    return this
}


/**
 * When generating code for a parameter we often want to add some statements that would run directly
 * on a given variable if it is a singular type, or use a forEach loop if it is a list or array.
 *
 * e.g.
 *
 * singular.changeSeqNum = 0
 *
 * or
 *
 * list.forEach {
 *     it.changeSeqNum = 0
 * }
 *
 * @param param the ParameterSpec that gives the type and the variable name
 * @param codeBlocks codeBlocks that should be run against each component of the parameter if it is
 * a list or array, or directly against the parameter if it is singular. Each will be automatically
 * prefixed with the parameter name for singular components, or "it" for lists and arrays
 * @return this
 */
fun CodeBlock.Builder.addRunCodeBlocksOnParamComponents(param: ParameterSpec, vararg codeBlocks: CodeBlock) : CodeBlock.Builder {
    if(param.type.isListOrArray()) {
        beginControlFlow("${param.name}.forEach")
        codeBlocks.forEach {
            add("it.")
            add(it)
        }
        endControlFlow()
    }else {
        codeBlocks.forEach {
            add("${param.name}.")
            add(it)
        }
    }

    return this
}

/**
 * Add code that will create the table using a function that executes SQL (e.g. stmt.executeUpdate or
 * db.execSQL) to create a table and any indices specified. Indices can be specified by through
 * the indices argument (e.g. for those that come from the Entity annotation) and via the
 * entityTypeSpec for those that are specified using ColumnInfo(index=true) annotation.
 *
 * @param entityTypeSpec a TypeSpec that represents the entity a table is being created for
 * @param execSqlFn the literal string that should be added to call a function that runs SQL
 * @param dbProductType DoorDbType.SQLITE or POSTGRES
 * @param indices a list of IndexMirror representing the indices that should be added.
 */
fun CodeBlock.Builder.addCreateTableCode(entityTypeSpec: TypeSpec, execSqlFn: String,
                                         dbProductType: Int, indices: List<IndexMirror> = listOf()) : CodeBlock.Builder {
    add("$execSqlFn(%S)\n", entityTypeSpec.toCreateTableSql(dbProductType))
    indices.forEach {
        val indexName = if(it.name != "") {
            it.name
        }else {
            "index_${entityTypeSpec.name}_${it.value.joinToString(separator = "_", postfix = "", prefix = "")}"
        }

        add("$execSqlFn(%S)\n", "CREATE ${if(it.unique){ "UNIQUE " } else { "" } }INDEX $indexName" +
                " ON ${entityTypeSpec.name} (${it.value.joinToString()})")
    }

    entityTypeSpec.entityFields().forEach { field ->
        if(field.annotations.any { it.className == ColumnInfo::class.asClassName()
                        && it.members.findBooleanMemberValue("index") ?: false }) {
            add("_stmt.executeUpdate(%S)\n",
                    "CREATE INDEX index_${entityTypeSpec.name}_${field.name} ON ${entityTypeSpec.name} (${field.name})")
        }
    }

    return this
}

/**
 * Adds code that will create the triggers (to increment change sequence numbers and, if
 * required, insert into ChangeLog) on SQLite when a row on the given syncableEntityInfo is
 * inserted.
 *
 * @param syncableEntityInfo SyncableEntityInfo for the entity annotated @SyncableEntity
 * @param execSqlFn the code to run an SQL statement e.g. "db.execSQL"
 * @return this
 */
fun CodeBlock.Builder.addSyncableEntityInsertTriggersSqlite(execSqlFn: String, syncableEntityInfo: SyncableEntityInfo): CodeBlock.Builder {
    val localCsnFieldName = syncableEntityInfo.entityLocalCsnField.name
    val primaryCsnFieldName = syncableEntityInfo.entityMasterCsnField.name
    val entityName = syncableEntityInfo.syncableEntity.simpleName
    val pkFieldName = syncableEntityInfo.entityPkField.name
    val tableId = syncableEntityInfo.tableId

    add("$execSqlFn(%S)\n", """
            CREATE TRIGGER INS_LOC_${syncableEntityInfo.tableId}
            AFTER INSERT ON $entityName
            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0) AND
                NEW.$localCsnFieldName = 0)
            BEGIN
                UPDATE $entityName
                SET $primaryCsnFieldName = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = $tableId)
                WHERE $pkFieldName = NEW.$pkFieldName;
                
                UPDATE SqliteChangeSeqNums
                SET sCsnNextPrimary = sCsnNextPrimary + 1
                WHERE sCsnTableId = $tableId;
            END
        """.trimIndent())

    add("$execSqlFn(%S)\n", """
            CREATE TRIGGER INS_PRI_${syncableEntityInfo.tableId}
            AFTER INSERT ON $entityName
            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1) AND
                NEW.$primaryCsnFieldName = 0)
            BEGIN
                UPDATE $entityName
                SET $primaryCsnFieldName = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = $tableId)
                WHERE $pkFieldName = NEW.$pkFieldName;
                
                UPDATE SqliteChangeSeqNums
                SET sCsnNextPrimary = sCsnNextPrimary + 1
                WHERE sCsnTableId = $tableId;
                
                ${syncableEntityInfo.sqliteChangeLogInsertSql};
            END
        """.trimIndent())

    return this
}

fun CodeBlock.Builder.addSyncableEntityFunctionPostgres(execSqlFn: String, syncableEntityInfo: SyncableEntityInfo) : CodeBlock.Builder {
    add("$execSqlFn(%S)\n", """CREATE OR REPLACE FUNCTION 
                    | inccsn_${syncableEntityInfo.tableId}_fn() RETURNS trigger AS $$
                    | BEGIN  
                    | UPDATE ${syncableEntityInfo.syncableEntity.simpleName} SET ${syncableEntityInfo.entityLocalCsnField.name} =
                    | (SELECT CASE WHEN (SELECT master FROM SyncNode) THEN NEW.${syncableEntityInfo.entityLocalCsnField.name} 
                    | ELSE NEXTVAL('${syncableEntityInfo.syncableEntity.simpleName}_lcsn_seq') END),
                    | ${syncableEntityInfo.entityMasterCsnField.name} = 
                    | (SELECT CASE WHEN (SELECT master FROM SyncNode) 
                    | THEN NEXTVAL('${syncableEntityInfo.syncableEntity.simpleName}_mcsn_seq') 
                    | ELSE NEW.${syncableEntityInfo.entityMasterCsnField.name} END)
                    | WHERE ${syncableEntityInfo.entityPkField.name} = NEW.${syncableEntityInfo.entityPkField.name};
                    | INSERT INTO ChangeLog(chTableId, chEntityPk, dispatched, chTime) 
                    | SELECT ${syncableEntityInfo.tableId}, NEW.${syncableEntityInfo.entityPkField.name}, false, cast(extract(epoch from now()) * 1000 AS BIGINT)
                    | WHERE COALESCE((SELECT master From SyncNode LIMIT 1), false);
                    | RETURN null;
                    | END $$
                    | LANGUAGE plpgsql
                """.trimMargin())

    return this
}


/**
 * Adds code that will create the triggers (to increment change sequence numbers and, if
 * required, insert into ChangeLog) on SQLite when a row on the given syncableEntityInfo is updated.
 *
 * @param syncableEntityInfo SyncableEntityInfo for the entity annotated @SyncableEntity
 * @param execSqlFn the code to run an SQL statement e.g. "db.execSQL"
 * @return this
 */
fun CodeBlock.Builder.addSyncableEntityUpdateTriggersSqlite(execSqlFn: String, syncableEntityInfo: SyncableEntityInfo) : CodeBlock.Builder {
    val localCsnFieldName = syncableEntityInfo.entityLocalCsnField.name
    val primaryCsnFieldName = syncableEntityInfo.entityMasterCsnField.name
    val entityName = syncableEntityInfo.syncableEntity.simpleName
    val pkFieldName = syncableEntityInfo.entityPkField.name
    val tableId = syncableEntityInfo.tableId

    add("$execSqlFn(%S)\n", """
            CREATE TRIGGER UPD_LOC_$tableId
            AFTER UPDATE ON $entityName
            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 0)
                AND (NEW.$localCsnFieldName == OLD.$localCsnFieldName OR
                    NEW.$localCsnFieldName == 0))
            BEGIN
                UPDATE $entityName
                SET $localCsnFieldName = (SELECT sCsnNextLocal FROM SqliteChangeSeqNums WHERE sCsnTableId = $tableId) 
                WHERE $pkFieldName = NEW.$pkFieldName;
                
                UPDATE SqliteChangeSeqNums 
                SET sCsnNextLocal = sCsnNextLocal + 1
                WHERE sCsnTableId = $tableId;
            END
        """.trimIndent())

    add("$execSqlFn(%S)\n", """
            CREATE TRIGGER UPD_PRI_$tableId
            AFTER UPDATE ON $entityName
            FOR EACH ROW WHEN (((SELECT CAST(master AS INTEGER) FROM SyncNode) = 1)
                AND (NEW.$primaryCsnFieldName == OLD.$primaryCsnFieldName OR
                    NEW.$primaryCsnFieldName == 0))
            BEGIN
                UPDATE $entityName
                SET $primaryCsnFieldName = (SELECT sCsnNextPrimary FROM SqliteChangeSeqNums WHERE sCsnTableId = $tableId)
                WHERE $pkFieldName = NEW.$pkFieldName;
                
                UPDATE SqliteChangeSeqNums
                SET sCsnNextPrimary = sCsnNextPrimary + 1
                WHERE sCsnTableId = $tableId;
                
                ${syncableEntityInfo.sqliteChangeLogInsertSql};
            END
        """.trimIndent())

    return this
}


/**
 * Add to the codeblock to create a line that will execute SQL to insert a row into SqliteChangeSeqNums
 * for the given SyncableEntity
 *
 * @param execSqlFn The name of the function to call to execute SQL e.g. "db.execSQL
 * @param syncableEntityInfo the syncableentityinfo for this row
 * @param preserveCurrentMaxLocalCsn if true, then use a query to set the next local change sequence
 * number to be one higher than the current maximum found in all rows. This should be true when this
 * is used as part of a migration, false otherwise.
 */
internal fun CodeBlock.Builder.addReplaceSqliteChangeSeqNums(execSqlFn: String,
                                                             syncableEntityInfo: SyncableEntityInfo,
                                                             preserveCurrentMaxLocalCsn: Boolean = false): CodeBlock.Builder {
    var replaceSql = "REPLACE INTO SqliteChangeSeqNums(sCsnTableId, sCsnNextLocal, sCsnNextPrimary)" +
            " VALUES(${syncableEntityInfo.tableId}, "

    if(preserveCurrentMaxLocalCsn) {
        replaceSql += "(SELECT COALESCE(" +
                "(SELECT MAX(${syncableEntityInfo.entityLocalCsnField.name}) FROM ${syncableEntityInfo.syncableEntity.simpleName})" +
                ", 0) + 1),"
    }else {
        replaceSql += "1,"
    }

    replaceSql += " 1)"

    add("$execSqlFn(%S)\n", replaceSql)

    return this
}


/**
 * Generates a CodeBlock that will make KTOR HTTP Client Request for a DAO method. It will set
 * the correct URL (e.g. endpoint/DatabaseName/DaoName/methodName and parameters (including the request body
 * if required). It will decide between using get or post based on the parameters.
 *
 * @param funSpec the FunSpec that represents the function for which we expect an endpoint on
 * the server
 * @param httpClientVarName variable name to access a KTOR httpClient
 * @param httpEndpointVarName variable name for the base API endpoint
 * @param dbPathVarName The path from the endpoint to the specific database
 * @param daoName The name of the DAO to which funSpec belongs
 */
internal fun CodeBlock.Builder.addKtorRequestForFunction(
                                                   funSpec: FunSpec,
                                                   httpClientVarName: String = "_httpClient",
                                                   httpEndpointVarName: String = "_endpoint",
                                                   dbPathVarName: String,
                                                   daoName: String,
                                                   useKotlinxListSerialization: Boolean = false,
                                                   kotlinxSerializationJsonVarName: String = "",
                                                   useMultipartPartsVarName: String? = null,
                                                   addDbVersionParamName: String? = "_db",
                                                   addClientIdHeaderVar: String? = null): CodeBlock.Builder {

    //Begin creation of the HttpStatement call that will set the URL, parameters, etc.
    val nonQueryParams =  funSpec.parameters.filter { !it.type.isHttpQueryQueryParam() }

    //The type of the response we expect from the server.
    val httpResultType = funSpec.returnType?.unwrapLiveDataOrDataSourceFactory() ?: UNIT

    val httpMemberFn = if(nonQueryParams.isEmpty()) {
        if(httpResultType.isNullable == true) {
            CLIENT_GET_NULLABLE_MEMBER_NAME
        }else {
            CLIENT_GET_MEMBER_NAME
        }
    }else {
        if(httpResultType.isNullable == true) {
            CLIENT_POST_NULLABLE_MEMBER_NAME
        }else {
            CLIENT_POST_MEMBER_NAME
        }
    }

    beginControlFlow("$httpClientVarName.%M<%T>",
            httpMemberFn, httpResultType)
    beginControlFlow("url")
    add("%M($httpEndpointVarName)\n", MemberName("io.ktor.http", "takeFrom"))
    add("encodedPath = \"\${encodedPath}\${$dbPathVarName}/%L/%L\"\n", daoName, funSpec.name)
    endControlFlow()

    if(addDbVersionParamName != null) {
        add("%M($addDbVersionParamName)\n",
                MemberName("com.ustadmobile.door.ext", "dbVersionHeader"))
    }

    if(addClientIdHeaderVar != null) {
        add("%M(%S, $addClientIdHeaderVar)\n", MemberName("io.ktor.client.request", "header"),
                "x-nid")
    }

    funSpec.parameters.filter { it.type.isHttpQueryQueryParam() }.forEach {
        val paramType = it.type
        val isList = paramType is ParameterizedTypeName && paramType.rawType == List::class.asClassName()

        val paramsCodeblock = CodeBlock.builder()
        var paramVarName = it.name
        if(isList) {
            paramsCodeblock.add("${it.name}.forEach { ")
            paramVarName = "it"
            if(paramType != String::class.asClassName()) {
                paramVarName += ".toString()"
            }
        }

        paramsCodeblock.add("%M(%S, $paramVarName)\n",
                MemberName("io.ktor.client.request", "parameter"),
                it.name)
        if(isList) {
            paramsCodeblock.add("} ")
        }
        paramsCodeblock.add("\n")
        addWithNullCheckIfNeeded(it.name, it.type, paramsCodeblock.build())
    }

    val requestBodyParam = funSpec.parameters.firstOrNull { !it.type.isHttpQueryQueryParam() }

    if(requestBodyParam != null) {
        val requestBodyParamType = requestBodyParam.type

        val writeBodyCodeBlock = if(useMultipartPartsVarName != null) {
            CodeBlock.of("body = %T($useMultipartPartsVarName)\n",
                    MultiPartFormDataContent::class)
        }else if(useKotlinxListSerialization && requestBodyParamType is ParameterizedTypeName
                && requestBodyParamType.rawType == List::class.asClassName()) {
            val entityComponentType = resolveEntityFromResultType(requestBodyParamType).javaToKotlinType()
            val serializerFnCodeBlock = if(entityComponentType in QUERY_SINGULAR_TYPES) {
                CodeBlock.of("%M()", MemberName("kotlinx.serialization", "serializer"))
            }else {
                CodeBlock.of("serializer()")
            }
            CodeBlock.of("body = %T(_json.stringify(%T.%L.%M, ${requestBodyParam.name}), %T.Application.Json.%M())\n",
                    TextContent::class, entityComponentType,
                    serializerFnCodeBlock,
                    MemberName("kotlinx.serialization.builtins", "list"),
                    ContentType::class,
                    MemberName("com.ustadmobile.door.ext", "withUtf8Charset"))
        }else {
            CodeBlock.of("body = %M().write(${requestBodyParam.name}, %T.Application.Json.%M())\n",
                    MemberName("io.ktor.client.features.json", "defaultSerializer"),
                    ContentType::class, MemberName("com.ustadmobile.door.ext", "withUtf8Charset"))
        }

        addWithNullCheckIfNeeded(requestBodyParam.name, requestBodyParam.type,
                writeBodyCodeBlock)
    }

    endControlFlow()

    return this
}


/**
 * Shorthand to begin a runBlocking control flow
 */
fun CodeBlock.Builder.beginRunBlockingControlFlow() =
        add("%M·{\n", MemberName("kotlinx.coroutines", "runBlocking"))
                .indent()

