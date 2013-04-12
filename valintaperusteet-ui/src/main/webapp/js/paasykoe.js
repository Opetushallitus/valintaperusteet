app.factory('PaasykoeModel', function(PaasykoeValinnanvaihe, Paasykoe) {

	var model = new function() {
		this.paasykoe = {};

		
	}	

	return model;

});

function PaasykoeController($scope, $location, $routeParams) {
	$scope.paasykoeModel = $routeParams.id;

	$scope.cancel = function () {
		$location.path("/valintaryhma/" + $scope.valintaryhmaOid + "/paasykoevalinnanvaihe/" /*+ $scope.model.paasykoevalinnanvaihe.oid */);
	}
}