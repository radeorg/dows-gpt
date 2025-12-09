package org.dows.gpt;

import org.dows.member.api.user.UserMemberMetricsApi;
import org.dows.member.response.MemberMetricsGetResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMemberMetricsMock implements UserMemberMetricsApi {

    @Override
    public MemberMetricsGetResponse getNewest() {
        return null;
    }

    @Override
    public void addUsedDailyMatchCount(int matchNum) {

    }

    @Override
    public void addUsedActiveInviteCount() {

    }

    @Override
    public void subUsedActiveInviteCount(Long accountInstanceId) {

    }

    @Override
    public void addUsedCreationJdCount() {

    }

    @Override
    public void subUsedCreationJdCount() {

    }

    @Override
    public void validateUploadPermission(int uploadNum) {

    }

    @Override
    public void validateMatchJdPermission(int matchNum) {

    }

    @Override
    public void validateCreationJdPermission() {

    }

    @Override
    public void validateInterviewPermission() {

    }

    @Override
    public void validatePushEmailPermission(Long accountInstanceId) {

    }
}
