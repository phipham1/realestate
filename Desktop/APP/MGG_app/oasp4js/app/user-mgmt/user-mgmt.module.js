angular.module('app.user-mgmt', ['app.main', 'app.user-mgmt.templates'], 
	function (ROLES, $stateProvider, oaspTranslationProvider, oaspAuthorizationServiceProvider) {
    'use strict';
    oaspTranslationProvider.enableTranslationForModule('user-mgmt');

    $stateProvider.state('userMgmt', {
        abstract: true,
        url: '/user-mgmt',
        template: '<ui-view/>'
    });

    $stateProvider.state('userMgmt.search', oaspAuthorizationServiceProvider.usersHavingAnyRoleOf(ROLES.WAITER).mayGoToStateDefinedAs({
        url: '/user-search',
        templateUrl: 'user-mgmt/user-search/user-search.html',
        controller: 'UserSearchCntl',
        controllerAs: 'USC',
        resolve: {
            currentUsers: ['users', function (users) {
                return users.loadUsers().then(function (result) {
                    return result;
                });
            }]
        }
    }));

    $stateProvider.state('userMgmt.create', {
        url: '/user-create',
        templateUrl: 'user-mgmt/user-create/user-create.html',
        controller: 'UserCreateCntl',
        controllerAs: 'UCC'
    });

});
