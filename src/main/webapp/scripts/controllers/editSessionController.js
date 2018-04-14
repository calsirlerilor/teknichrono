

angular.module('frontend').controller('EditSessionController', function($scope, $routeParams, $location, flash, SessionResource , PilotResource, EventResource, LocationResource, ChronometerResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.session = new SessionResource(self.original);
            PilotResource.queryAll(function(items) {
                $scope.pilotsSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.session.pilots){
                        $.each($scope.session.pilots, function(idx, element) {
                            if(item.id == element.id) {
                                $scope.pilotsSelection.push(labelObject);
                                $scope.session.pilots.push(wrappedObject);
                            }
                        });
                        self.original.pilots = $scope.session.pilots;
                    }
                    return labelObject;
                });
            });
            EventResource.queryAll(function(items) {
                $scope.eventSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.session.event && item.id == $scope.session.event.id) {
                        $scope.eventSelection = labelObject;
                        $scope.session.event = wrappedObject;
                        self.original.event = $scope.session.event;
                    }
                    return labelObject;
                });
            });
            LocationResource.queryAll(function(items) {
                $scope.locationSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.session.location && item.id == $scope.session.location.id) {
                        $scope.locationSelection = labelObject;
                        $scope.session.location = wrappedObject;
                        self.original.location = $scope.session.location;
                    }
                    return labelObject;
                });
            });
            ChronometerResource.queryAll(function(items) {
                $scope.chronometersSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.session.chronometers){
                        $.each($scope.session.chronometers, function(idx, element) {
                            if(item.id == element.id) {
                                $scope.chronometersSelection.push(labelObject);
                                $scope.session.chronometers.push(wrappedObject);
                            }
                        });
                        self.original.chronometers = $scope.session.chronometers;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The session could not be found.'});
            $location.path("/Sessions");
        };
        SessionResource.get({SessionId:$routeParams.SessionId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.session);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The session was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.session.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Sessions");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The session was deleted.'});
            $location.path("/Sessions");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.session.$remove(successCallback, errorCallback);
    };
    
    $scope.pilotsSelection = $scope.pilotsSelection || [];
    $scope.$watch("pilotsSelection", function(selection) {
        if (typeof selection != 'undefined' && $scope.session) {
            $scope.session.pilots = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.session.pilots.push(collectionItem);
            });
        }
    });
    $scope.$watch("eventSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.session.event = {};
            $scope.session.event.id = selection.value;
        }
    });
    $scope.$watch("locationSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.session.location = {};
            $scope.session.location.id = selection.value;
        }
    });
    $scope.chronometersSelection = $scope.chronometersSelection || [];
    $scope.$watch("chronometersSelection", function(selection) {
        if (typeof selection != 'undefined' && $scope.session) {
            $scope.session.chronometers = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.session.chronometers.push(collectionItem);
            });
        }
    });
    $scope.sessionTypeList = [
        "TIME_TRIAL",  
        "RACE"  
    ];
    
    $scope.get();
});