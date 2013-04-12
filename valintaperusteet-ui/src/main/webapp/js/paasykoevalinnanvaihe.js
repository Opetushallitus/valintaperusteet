
app.factory('HakukohdePaasykoeValinnanvaiheModel', function(/*PaasykoeValinnanvaihe, Paasykoe*/) {
	
	var model = new function() {
		this.paasykoevalinnanvaihe = {}
		this.paasykokeet = [];

		this.refresh = function(oid) {
			if(!oid) {
				model.paasykoevalinnanvaihe = {};
				model.paasykokeet = [];
			} else {

				/*
				PaasykoeValinnanvaihe.get({oid: oid}, function(result) {
					model.paasykoevalinnanvaihe = result;
				});

				Paasykoe.get({oid: oid}, function(result) {
					model.paasykoe = result;
				});
				*/
			}
		}

	}

	return model;

});




function HakukohdePaasykoeValinnanvaiheController($scope, $location, $routeParams, PaasykoeValinnanvaiheModel) {
	$scope.valintaryhmaOid = $routeParams.id;
	$scope.model = PaasykoeValinnanvaiheModel; 
	//$scope.model.refresh();

	$scope.cancel = function() {
        $location.path("/valintaryhma/" + $scope.valintaryhmaOid);
    }

    $scope.addPaasykoe = function() {
    	$location.path("/valintaryhma/" + $scope.valintaryhmaOid + "/paasykoevalinnanvaihe/" + /*$scope.model.valinnanvaihe.oid + / */ "/paasykoe/");
    }

}


app.factory('HakukohdePaasykoeValinnanvaiheCreatorModel', function(/*PaasykoeValinnanvaihe, Paasykoe*/) {
	var model = new function() {
		this.paasykoevalinnanvaihe = {};

		this.refresh = function() {
			model.paasykoevalinnanvaihe = {};
		}

		this.persistPaasykoeValinnanvaihe = function() {

		}

	}

	return model;
});