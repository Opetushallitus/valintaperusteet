app.factory('ValintakoeModel', function(ValintakoeValinnanvaihe, Valintakoe) {

	var model = new function() {
		this.valintakoe = {};

		
	}	

	return model;

});

function ValintakoeController($scope, $location, $routeParams) {
	$scope.valintakoeModel = $routeParams.id;

	$scope.cancel = function () {
		$location.path("/valintaryhma/" + $scope.valintaryhmaOid + "/valintakoevalinnanvaihe/" /*+ $scope.model.paasykoevalinnanvaihe.oid */);
	}
}