angular.module('app.user-mgmt').factory('users', function (userManagementRestService) {
    'use strict';

    var loadedUsers = [];

    return {
        loadUsers: function () {
            return userManagementRestService.getUsers().then(function (response) {
                angular.copy(response.data, loadedUsers);
                return loadedUsers;
            });
        },

        createUser: function (user) {
            return userManagementRestService.addUser(user).then(function (response) {
                return response;
            });
        },

        delegateGroupsToUser: function (uid) {
            return userManagementRestService.delegateGroupsToUser(uid).then(function (response) {
                return response;
            });
        }
    }
        
});
