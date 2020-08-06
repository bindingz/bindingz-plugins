package project.to.test;

import io.bindingz.api.annotations.Contract;

@Contract(contractName = "ProjectToTestDto", owner = "bindingz-maven-plugin-example", version = "5.0-SNAPSHOT")
public class ProjectToTestDto {
    private float accountId;
    private String code;
    private String startDay;
    private String endDay;
    private String groupId;
    private String groupName;

    public float getAccountId() {
        return accountId;
    }

    public void setAccountId(float accountId) {
        this.accountId = accountId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
