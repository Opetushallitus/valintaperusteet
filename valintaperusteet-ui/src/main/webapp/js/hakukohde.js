

app.factory('HakukohdeModel', function(Hakukohde, Valintaryhma, HakukohdeValinnanvaihe, Valinnanvaihe, ValinnanvaiheJarjesta, HakukohdeKuuluuSijoitteluun) {
    var model = new function()  {
        
        this.parentValintaryhma = {};
        this.hakukohde = {};
        this.valinnanvaiheet = [];

        this.refresh = function(oid) {
            Hakukohde.get({oid: oid}, function(result) {
                model.hakukohde = result;
                
                Valintaryhma.get({oid: model.hakukohde.valintaryhma_id}, function(result) {
                    model.parentValintaryhma = result;
                });

                kuuluuSijoitteluun(oid);
            });

            model.refreshValinnanvaiheet(oid);
        };
        this.refreshIfNeeded = function(oid) {
            if(oid != model.hakukohde.oid) {
                this.refresh(oid);
            } else {
                kuuluuSijoitteluun(oid);
            }
        };

        this.refreshValinnanvaiheet = function(oid) {
            HakukohdeValinnanvaihe.get({parentOid: oid}, function(result) {
                model.valinnanvaiheet = result;
            });
        };

        this.persistHakukohde = function() {
            Hakukohde.post(model.hakukohde, function(result) {});
            if(model.valinnanvaiheet.length > 0) {
                ValinnanvaiheJarjesta.post(getValinnanvaiheOids(), function(result) {});
                for(var i = 0 ; i < model.valinnanvaiheet.length ; ++i) {
                    Valinnanvaihe.post(model.valinnanvaiheet[i], function(){

                    });
                }
            }
        };
        this.remove = function(vaihe) {
            Valinnanvaihe.delete({oid: vaihe.oid} ,function(result) {
                for(i in model.valinnanvaiheet) {
                    if(vaihe.oid === model.valinnanvaiheet[i].oid) {
                        model.valinnanvaiheet.splice(i,1);
                    }
                }
            });
        };

        this.getValinnanvaiheType = function(valinnanvaihe) {
            var type;
            if(valinnanvaihe.valinnanVaiheTyyppi === "TAVALLINEN") {
                type = "valinnanvaihe";
            } else {
                type = "valintakoevalinnanvaihe";
            }
            return type;
        }

    };

    function getValinnanvaiheOids() {
        var oids = [];
        for (var i = 0 ; i < model.valinnanvaiheet.length ; ++i) {
            oids.push(model.valinnanvaiheet[i].oid);
        }
        return oids;
    }

    function kuuluuSijoitteluun(oid) {
        HakukohdeKuuluuSijoitteluun.get({oid: oid}, function(result) {
            model.hakukohde.kuuluuSijoitteluun = result.sijoitteluun;
        });
    }

    return model;
});



function HakukohdeController($scope, $location, $routeParams, HakukohdeModel) {
    $scope.hakukohdeOid = $routeParams.hakukohdeOid;
    $scope.model = HakukohdeModel;
    $scope.model.refreshIfNeeded($scope.hakukohdeOid);

    $scope.submit = function() {
        $scope.model.persistHakukohde();
    }
    $scope.cancel = function() {
        $location.path("/");
    }
    $scope.lisaaValinnanVaihe = function() {
        $location.path("/hakukohde/" + $scope.hakukohdeOid + "/valinnanvaihe/");
    }
    $scope.lisaaValintakoeValinnanVaihe = function() {
        $location.path("/hakukohde/" + $scope.hakukohdeOid + "/valintakoevalinnanvaihe/");
    }

}

function HakukohdeLaskentakaavaController($scope, $location, $routeParams, Laskentakaava) {
     $scope.hakukohdeOid = $routeParams.hakukohdeOid;
}








app.factory('HakukohdeCreatorModel', function($location, NewHakukohde, Treemodel) {
    var model = new function()  {
        this.hakukohde = {};

        this.refresh = function() {
            model.hakukohde = {};
        }

        this.persistHakukohde = function(valintaryhmaOid) {
            NewHakukohde.insert(model.hakukohde, function(result) {
                Treemodel.refresh();
                $location.path("/");
            }, function(error){
                // Tänne ei tulla jostain syystä vaikka tulis 500 error
            });
        }

    }

    return model;
});

function HakukohdeCreatorController($scope, $location, $routeParams, HakukohdeCreatorModel) {
    $scope.valintaryhmaOid = $routeParams.valintaryhmaOid;
    $scope.model = HakukohdeCreatorModel;
    $scope.model.refresh();

    $scope.cancel = function() {
        $location.path("/");
    };

    $scope.submit = function() {
        $scope.model.persistHakukohde($scope.valintaryhmaOid);
    }
}