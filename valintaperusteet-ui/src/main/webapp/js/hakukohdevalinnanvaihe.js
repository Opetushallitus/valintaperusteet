


function ValinnanVaiheController($scope, $location, $routeParams, HakukohdeValinnanVaiheModel, HakukohdeModel) {
    $scope.hakukohdeOid = $routeParams.hakukohdeOid;
    $scope.valinnanvaiheOid = $routeParams.valinnanvaiheOid;

    $scope.model = HakukohdeValinnanVaiheModel;
    $scope.model.refreshIfNeeded($scope.valinnanvaiheOid);

    $scope.submit = function() {
        $scope.model.persistValinnanvaihe($scope.hakukohdeOid, HakukohdeModel.valinnanvaiheet);
    }

    $scope.cancel = function() {
        $location.path("/hakukohde/" + $scope.hakukohdeOid);
    }

    $scope.addJono = function() {
        $location.path("/hakukohde/" + $scope.hakukohdeOid + "/valinnanvaihe/" + $scope.model.valinnanvaihe.oid + "/valintatapajono/");
    }

    $scope.modifyJono = function(oid) {
        $location.path("/hakukohde/" + $scope.hakukohdeOid + "/valinnanvaihe/" + $scope.model.valinnanvaihe.oid + "/valintatapajono/" + oid);
    }
}

app.factory('HakukohdeValinnanVaiheModel', function(Valinnanvaihe, Valintatapajono, ValinnanvaiheValintatapajono, HakukohdeValinnanvaihe, ValinnanvaiheKuuluuSijoitteluun, ValintatapajonoJarjesta) {
    var model = new function() {
        this.valinnanvaihe = {};
        this.valintatapajonot = [];


        this.refresh = function(oid) {
            if(!oid) {
                model.valinnanvaihe = {};
                model.valintatapajonot = [];
            } else {
                
                Valinnanvaihe.get({oid: oid}, function(result) {
                    model.valinnanvaihe = result;
                    kuuluuSijoitteluun(oid);
                });
                
                ValinnanvaiheValintatapajono.get({parentOid: oid}, function(result) {
                    model.valintatapajonot = result;
                }); 
            }
        }

        this.refreshIfNeeded = function(oid) {
            if( oid !== model.valinnanvaihe.oid ) {
                this.refresh(oid);
            } else {
                kuuluuSijoitteluun(oid);
            }
        };

        this.remove = function(jono) {
            Valintatapajono.delete({oid: jono.oid}, function(result) {
                for(i in model.valintatapajonot) {
                    if(jono.oid === model.valintatapajonot[i].oid) {
                        model.valintatapajonot.splice(i,1);
                    }
                }
            });
        };

        this.persistValinnanvaihe = function(hakukohdeParentOid, valinnanvaiheet) {
            if(!model.valinnanvaihe.oid) {
                model.valinnanvaihe.aktiivinen = true;
                HakukohdeValinnanvaihe.insert(
                    {
                        "parentOid": hakukohdeParentOid,
                        "tyyppi": "TAVALLINEN"
                    }, 
                    model.valinnanvaihe, 
                    function(result) {
                        model.valinnanvaihe = result;
                        valinnanvaiheet.push(result);
                    }
                );
            } else {
                Valinnanvaihe.post(model.valinnanvaihe, function(result) {
                    model.valinnanvaihe = result;
                    // Päivitetään myös edellisen näkymän modeli
                    var i;
                    for(i in valinnanvaiheet) {
                        if(result.oid === valinnanvaiheet[i].oid) {
                            valinnanvaiheet[i] = result;
                        }
                    }
                });

                ValintatapajonoJarjesta.post(getValintatapajonoOids(), function(result) { });

                for(var i = 0; i < model.valintatapajonot.length ; ++i) {
                    Valintatapajono.post(model.valintatapajonot[i], function(result) { });
                }
            }
        };

        function kuuluuSijoitteluun(oid) {
            ValinnanvaiheKuuluuSijoitteluun.get({oid: oid}, function(result) {
                model.valinnanvaihe.kuuluuSijoitteluun = result.sijoitteluun;
            });
        }

        function getValintatapajonoOids() {
            var oids = [];
            for (var i = 0 ; i < model.valintatapajonot.length ; ++i) {
                oids.push(model.valintatapajonot[i].oid);
            }
            return oids;
        }
    }
    return model;
});



app.factory('HakukohdeValintakoeValinnanvaiheModel', function(/*HakukohdeValintakoeValinnanvaihe, HakukohdeValintakoe*/) {
    
    var model = new function() {
        this.valintakoevalinnanvaihe = {}
        this.valintakokeet = [];

        this.refresh = function(oid) {
            if(!oid) {
                model.valintakoevalinnanvaihe = {};
                model.valintakokeet = [];
            } else {

                /*
                ValintakoeValinnanvaihe.get({oid: oid}, function(result) {
                    model.valintakoevalinnanvaihe = result;
                });

                Valintakoe.get({oid: oid}, function(result) {
                    model.valintakoe = result;
                });
                */
            }
        }

        this.refreshIfNeeded = function(oid) {
            if(oid !== this.valintakoevalinnanvaihe.oid) {
                model.refresh(oid);
            }
        }



    }

    return model;

});


function HakukohdeValintakoeValinnanvaiheController($scope, $location, $routeParams, HakukohdeValintakoeValinnanvaiheModel) {
    $scope.valintaryhmaOid = $routeParams.id;
    $scope.model = HakukohdeValintakoeValinnanvaiheModel; 
    //$scope.model.refresh();

    $scope.cancel = function() {
        $location.path("/valintaryhma/" + $scope.valintaryhmaOid);
    }

    $scope.addValintakoe = function() {
        $location.path("/valintaryhma/" + $scope.valintaryhmaOid + "/valintakoevalinnanvaihe/" + /*$scope.model.valinnanvaihe.oid + / */ "/valintakoe/");
    }

}


