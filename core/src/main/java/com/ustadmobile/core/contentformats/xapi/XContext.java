package com.ustadmobile.core.contentformats.xapi;

import java.util.Map;

public class XContext {

    private Actor instructor;

    private String registration;

    private String language;

    private String platform;

    private String revision;

    private Actor team;

    private XObject statement;

    private ContextActivity contextActivities;

    private Map<String, String> extensions;

    public Actor getInstructor() {
        return instructor;
    }

    public void setInstructor(Actor instructor) {
        this.instructor = instructor;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public XObject getStatement() {
        return statement;
    }

    public void setStatement(XObject statement) {
        this.statement = statement;
    }

    public ContextActivity getContextActivities() {
        return contextActivities;
    }

    public void setContextActivities(ContextActivity contextActivities) {
        this.contextActivities = contextActivities;
    }

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, String> extensions) {
        this.extensions = extensions;
    }

    public Actor getTeam() {
        return team;
    }

    public void setTeam(Actor team) {
        this.team = team;
    }
}
