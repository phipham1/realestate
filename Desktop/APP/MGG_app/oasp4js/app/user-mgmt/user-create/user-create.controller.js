angular.module('app.user-mgmt').controller('UserCreateCntl',
    function ($scope, globalSpinner, users, $state) {
        'use strict';

        var uccSelf = this;
        uccSelf.user = {};

        uccSelf.submit = function () {
            globalSpinner.decorateCallOfFunctionReturningPromise(function () {
                return users.createUser(uccSelf.user);
            }).then(function () {
                uccSelf.goToSearchView();
            });
        };

        uccSelf.goToSearchView = function () {
            $state.go('userMgmt.search');
        };

    });