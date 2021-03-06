
angular.module('frontend').controller('NewBeaconController', function ($scope, $location, locationParser, flash, BeaconResource, PilotResource, PingResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.beacon = $scope.beacon || {};

    $scope.pilotList = PilotResource.queryAll(function (items) {
        $scope.pilotSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.firstName + ' ' + item.lastName
            });
        });
    });
    $scope.$watch("pilotSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.beacon.pilot = {};
            $scope.beacon.pilot.id = selection.value;
        }
    });

    $scope.pingsList = PingResource.queryAll(function (items) {
        $scope.pingsSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.id
            });
        });
    });
    $scope.$watch("pingsSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.beacon.pings = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.beacon.pings.push(collectionItem);
            });
        }
    });


    $scope.save = function () {
        var successCallback = function (data, responseHeaders) {
            var id = locationParser(responseHeaders);
            flash.setMessage({ 'type': 'success', 'text': 'The beacon was created successfully.' });
            $location.path('/Beacons');
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        BeaconResource.save($scope.beacon, successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Beacons");
    };
});