app.factory('ValintakoeModel', function($q, Valintakoe, ValinnanvaiheValintakoe, Laskentakaava) {

	var model = new function() {
		this.valintakoe = {};
		this.laskentakaavat = [];

		this.refresh = function(oid) {
			if(!oid) {
				model.valintakoe = {};
				model.laskentakaavat = [];
			} else {
				Valintakoe.get({valintakoeOid: oid}, function(result) {
					model.valintakoe = result;
				});
				
				Laskentakaava.list({tyyppi: "TOTUUSARVOFUNKTIO"},function(result) {
                	model.laskentakaavat = result;
                });

			}
		}

		this.refreshIfNeeded = function(oid) {
			if(model.valintakoe.oid !== oid) {
				model.refresh(oid);
			}
		}

		this.persistValintakoe = function(parentValintakoeValinnanvaiheOid, valintakokeet) {
			var deferred = $q.defer();
			if(model.valintakoe.oid) {
				Valintakoe.update({valintakoeOid: model.valintakoe.oid}, model.valintakoe, function(result) {
					deferred.resolve();
				});	
				
			} else {
				var valintakoe = {
					tunniste: model.valintakoe.tunniste,
					nimi: model.valintakoe.nimi,
					kuvaus: model.valintakoe.kuvaus
				}

				ValinnanvaiheValintakoe.insert({valinnanvaiheOid: parentValintakoeValinnanvaiheOid},valintakoe, function(result) {
					var index;
					for(index in valintakokeet) {
						if(result.oid === valintakokeet[index].oid){
							valintakokeet[index] = result;
						}
					}
					deferred.resolve();
				});
			}
			return deferred.promise;
		}

		this.getParentGroupType = function(path) {
			
			var type;
			var pathArray = path.split("/");
			if(pathArray[1] === "valintaryhma") {
				type = "valintaryhma";
			} else {
				type = "hakukohde";
			}

			return type;
		}
		
	}	

	return model;

});

function ValintaryhmaValintakoeController($scope, $location, $routeParams, ValintakoeModel, ValintaryhmaValintakoeValinnanvaiheModel, HakukohdeValintakoeValinnanvaiheModel) {
	$scope.parentGroupOid = $routeParams.id; 
	$scope.valintakoeValinnanvaiheOid = $routeParams.valintakoevalinnanvaiheOid;
	$scope.valintakoeOid = $routeParams.valintakoeOid;
	$scope.model = ValintakoeModel;
	$scope.model.refreshIfNeeded($scope.valintakoeOid);

	$scope.submit = function() {
		var promise = $scope.model.persistValintakoe($scope.valintakoeValinnanvaiheOid, ValintaryhmaValintakoeValinnanvaiheModel.valintakokeet);
		promise.then(function(){
			$location.path("/" + $scope.model.getParentGroupType($location.$$path) + "/" + $scope.parentGroupOid + "/valintakoevalinnanvaihe/" + $scope.valintakoeValinnanvaiheOid);	
		});
		
	}

	$scope.cancel = function () {
		$location.path("/" + $scope.model.getParentGroupType($location.$$path) + "/" + $scope.parentGroupOid + "/valintakoevalinnanvaihe/" + $scope.valintakoeValinnanvaiheOid );
	}
}

function HakukohdeValintakoeController($scope, $location, $routeParams, ValintakoeModel, ValintaryhmaValintakoeValinnanvaiheModel, HakukohdeValintakoeValinnanvaiheModel) {
	$scope.parentGroupOid = $routeParams.hakukohdeOid; 
	$scope.valintakoeValinnanvaiheOid = $routeParams.valintakoevalinnanvaiheOid;
	$scope.valintakoeOid = $routeParams.id;
	$scope.model = ValintakoeModel;
	$scope.model.refreshIfNeeded($scope.valintakoeOid);

	$scope.submit = function() {
		var promise = $scope.model.persistValintakoe($scope.valintakoeValinnanvaiheOid, HakukohdeValintakoeValinnanvaiheModel.valintakokeet);
		promise.then(function() {
			$location.path("/" + $scope.model.getParentGroupType($location.$$path) + "/" + $scope.parentGroupOid + "/valintakoevalinnanvaihe/" + $scope.valintakoeValinnanvaiheOid);	
		});
		
	}

	$scope.cancel = function () {
		$location.path("/" + $scope.model.getParentGroupType($location.$$path) + "/" + $scope.parentGroupOid + "/valintakoevalinnanvaihe/" + $scope.valintakoeValinnanvaiheOid );
	}
}