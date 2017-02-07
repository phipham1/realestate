angular.module('app.user-mgmt').factory('userManagementRestService', function ($http, currentContextPath) {
    'use strict';

    var servicePath = currentContextPath.get() + 'services/rest/usermanagement/v1';

    return {

        getUsers: function () {
            return $http.get(servicePath + '/user');
        },

        addUser: function (user) {
            return $http.post(servicePath + '/user', user);
        },

        delegateGroupsToUser: function (uid) {
            return $http.post(servicePath + '/user/' + uid + '/groups');
        }

    }

});
