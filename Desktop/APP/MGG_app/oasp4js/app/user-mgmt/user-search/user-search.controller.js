angular.module('app.user-mgmt')
    .controller('UserSearchCntl', function ($scope, $state, users, currentUsers, globalSpinner) {
        'use strict';

        $scope.currentUsers = currentUsers;
        $scope.selectedItems = [];

        var selectedUser = function () {
            return $scope.selectedItems && $scope.selectedItems.length ? $scope.selectedItems[0] : undefined;
        };

        $scope.openCreateDialog = function () {
            $state.go('userMgmt.create');
        };

        $scope.buttonDefs = [
            {
                label: 'Neuen Nutzer anlegen',
                onClick: function () {
                    $scope.openCreateDialog();
                },
                isActive: function () {
                	return true;
                }
            },
            {
                label: 'Als Urlaubsvertretung setzen',
                onClick: function () {
                    globalSpinner.decorateCallOfFunctionReturningPromise(function () { //?
                        var result = users.delegateGroupsToUser(selectedUser().userId).then(function(){});
                        return result;
                    });
                },
                isActive: function () {
                	return selectedUser();
                }
            }
        ];
    });
