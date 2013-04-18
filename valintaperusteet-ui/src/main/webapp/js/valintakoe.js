app.factory('ValintakoeModel', function(Valintakoe) {

	var model = new function() {
		this.valintakoe = {};

		this.refresh = function(oid) {
			if(!oid) {
				model.valintakoe = {};
			} else {
				console.log("nothing yet");
			}
		}

		this.refreshIfNeeded = function(oid) {
			if(model.valintakoe.oid !== oid) {
				model.refresh(oid);
			}
		}

		this.persistValintakoe = function(parentValintakoeValinnanvaiheOid, valintakokeet) {
			if(model.valintakoe.oid) {
				console.log("should be updating valintakoe, no implementations yet");
			} else {
				var valintakoe = {
					tunniste: model.valintakoe.tunniste,
					nimi: model.valintakoe.nimi,
					kuvaus: model.valintakoe.kuvaus
				}

				Valintakoe.insert({valinnanvaiheOid: parentValintakoeValinnanvaiheOid},valintakoe, function(result) {
					var index;
					for(index in valintakokeet) {
						if(result.oid === valintakokeet[index].oid){
							valintakokeet[index] = result;
						}
					}
				});
			}
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

function ValintakoeController($scope, $location, $routeParams, ValintakoeModel, ValintaryhmaValintakoeValinnanvaiheModel, HakukohdeValintakoeValinnanvaiheModel) {
	$scope.parentGroupOid = $routeParams.id; //valintaryhmaOid or hakukohdeOid
	$scope.valintakoeValinnanvaiheOid = $routeParams.valintakoevalinnanvaiheOid;
	$scope.valintakoeOid = $routeParams.valintakoeOid;
	$scope.model = ValintakoeModel;
	$scope.model.refreshIfNeeded($scope.valintakoeOid);

	$scope.submit = function() {
		if($scope.model.getParentGroupType($location.$$path) === "valintaryhma") {
			$scope.model.persistValintakoe($scope.valintakoeValinnanvaiheOid, ValintaryhmaValintakoeValinnanvaiheModel.valintakokeet);
		} else {
			$scope.model.persistValintakoe($scope.valintakoeValinnanvaiheOid, HakukohdeValintakoeValinnanvaiheModel.valintakokeet);
		}
		$location.path("/" + $scope.model.getParentGroupType($location.$$path) + "/" + $scope.parentGroupOid + "/valintakoevalinnanvaihe/" + $scope.valintakoeValinnanvaiheOid);
	}

	$scope.cancel = function () {
		$location.path("/" + $scope.model.getParentGroupType($location.$$path) + "/" + $scope.parentGroupOid + "/valintakoevalinnanvaihe/" + $scope.valintakoeValinnanvaiheOid );
	}
}