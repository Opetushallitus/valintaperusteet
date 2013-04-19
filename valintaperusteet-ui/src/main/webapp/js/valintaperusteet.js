var app = angular.module('valintaperusteet', ['ngResource', 'loading']);

var SERVICE_URL_BASE = SERVICE_URL_BASE || ""
var TEMPLATE_URL_BASE = TEMPLATE_URL_BASE || ""

//Route configuration
app.config(function($routeProvider) {
        $routeProvider.

        //front page
        when('/etusivu', {controller:ValintaryhmaHakukohdeTreeController, templateUrl:TEMPLATE_URL_BASE + 'valintaryhmatlistaus.html'}).

        //edit valintaryhma
        when('/valintaryhma/:id', {controller:valintaryhmaController, templateUrl:TEMPLATE_URL_BASE + 'valintaryhmalomake.html'}).
        when('/valintaryhma/:id/sisaltyvat', {controller: ValintaryhmaChildrenController, templateUrl: TEMPLATE_URL_BASE + 'valintaryhmachildren.html'}).
        when('/valintaryhma/:valintaryhmaOid/laskentakaava', {controller: LaskentakaavaListController, templateUrl: TEMPLATE_URL_BASE + 'laskentakaava/laskentakaavalista.html'}).
        when('/valintaryhma/:valintaryhmaOid/laskentakaava/:laskentakaavaOid', {controller: LaskentakaavaController, templateUrl: TEMPLATE_URL_BASE + 'laskentakaava/laskentakaavalomake.html'}).
        when('/valintaryhma/:id/lisaaSisaltyvaValintaryhma', {controller:ValintaryhmaCreatorController, templateUrl: TEMPLATE_URL_BASE + 'valintaryhmalomake.html'}).
        when('/valintaryhma/:id/valinnanvaihe/:valinnanvaiheOid', {controller: valintaryhmaValinnanvaiheController, templateUrl: TEMPLATE_URL_BASE + 'valinnanvaihelomake.html'}).
        when('/valintaryhma/:id/valinnanvaihe/:valinnanvaiheOid/valintatapajono/:valintatapajonoOid', {controller: ValintaryhmaValintatapajonoController, templateUrl:TEMPLATE_URL_BASE + 'valintatapajono.html'}).
        when('/valintaryhma/:id/valinnanvaihe/:valinnanvaiheOid/valintatapajono/:valintatapajonoOid/jarjestyskriteeri/:jarjestyskriteeriOid', {controller:JarjestyskriteeriController, templateUrl:TEMPLATE_URL_BASE + 'jarjestyskriteeri.html'}).

        //valintakoevalinnanvaihe
        when('/valintaryhma/:id/valintakoevalinnanvaihe/:valintakoevalinnanvaiheOid', {controller: ValintaryhmaValintakoeValinnanvaiheController, templateUrl:TEMPLATE_URL_BASE + 'valintakoevalinnanvaihelomake.html'}).
        when('/valintaryhma/:id/valintakoevalinnanvaihe/:valintakoevalinnanvaiheOid/valintakoe/:valintakoeOid', {controller: ValintaryhmaValintakoeController, templateUrl:TEMPLATE_URL_BASE + 'valintakoelomake.html'}).
        when('/hakukohde/:hakukohdeOid/valintakoevalinnanvaihe/:valintakoevalinnanvaiheOid', {controller: HakukohdeValintakoeValinnanvaiheController, templateUrl:TEMPLATE_URL_BASE + 'valintakoevalinnanvaihelomake.html'}).
        when('/hakukohde/:hakukohdeOid/valintakoevalinnanvaihe/:valintakoevalinnanvaiheOid/valintakoe/:id', {controller: HakukohdeValintakoeController, templateUrl:TEMPLATE_URL_BASE + 'valintakoelomake.html'}).
		    
        // edit hakukohde
        when('/hakukohde/:hakukohdeOid', {controller:HakukohdeController, templateUrl:TEMPLATE_URL_BASE + 'hakukohdelomake.html'}).
//        when('/hakukohde/:hakukohdeOid/valinnanvaihe', {controller:HakukohdeController, templateUrl:TEMPLATE_URL_BASE + 'hakukohdelomake.html'}).
        when('/valintaryhma/:valintaryhmaOid/lisaaSisaltyvaHakukohde', {controller: HakukohdeCreatorController, templateUrl:TEMPLATE_URL_BASE + 'hakukohdelomake.html'}).
        when('/hakukohde/:hakukohdeOid/valinnanvaihe/:valinnanvaiheOid', {controller:ValinnanVaiheController, templateUrl:TEMPLATE_URL_BASE + 'valinnanvaihelomake.html'}).
        when('/hakukohde/:hakukohdeOid/valinnanvaihe/:valinnanvaiheOid/valintatapajono/:valintatapajonoOid', {controller: HakukohdeValintatapajonoController, templateUrl:TEMPLATE_URL_BASE + 'valintatapajono.html'}).
        when('/hakukohde/:hakukohdeOid/valinnanvaihe/:valinnanvaiheOid/valintatapajono/:valintatapajonoOid/jarjestyskriteeri/:jarjestyskriteeriOid', {controller:JarjestyskriteeriController, templateUrl:TEMPLATE_URL_BASE + 'jarjestyskriteeri.html'}).

        //laskentakaava
        when('/laskentakaava', {controller: LaskentakaavaListController, templateUrl: TEMPLATE_URL_BASE + 'laskentakaava/laskentakaavalista.html'}).
        when('/laskentakaava/:laskentakaavaOid', {controller: LaskentakaavaController, templateUrl:TEMPLATE_URL_BASE + 'laskentakaava/laskentakaavalomake.html'}).
		
		
        //else
        otherwise({redirectTo:'/etusivu'});
});


//rest resources


//Valintaryhma
app.factory('RootValintaryhmas', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/valintaryhma", {}, {
    get: {method: "GET", isArray: true,
        params: {
            paataso: true
        }
    }
  });
});
app.factory('ChildValintaryhmas', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/valintaryhma/:parentOid/lapsi", {parentOid: "@parentOid"}, {
    get: {method: "GET", isArray: true},
    insert: {method: "PUT"}
  });
});

app.factory('ChildHakukohdes', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/valintaryhma/:oid/hakukohde", {}, {
    get: {method: "GET", isArray: true}
  });
});
app.factory('Valintaryhma', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/valintaryhma/:oid", {oid: "@oid"}, {
    get: {method: "GET"}  ,
    post:{method: "POST"},
    insert: {method: "PUT"}
  });
});
app.factory('ParentValintaryhmas', function($resource) {
    return $resource(SERVICE_URL_BASE + "resources/valintaryhma/:parentOid/parents", {parentOid: "@parentOid"}, {
        get: {method: "GET", isArray: true}
    });
});

app.factory('ValintaryhmaValinnanvaihe', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/valintaryhma/:oid/valinnanvaihe", {oid: "@oid"}, {
    get: {method: "GET", isArray: true}
  });
});
app.factory('NewValintaryhmaValinnanvaihe', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/valintaryhma/:valintaryhmaOid/valinnanvaihe", {valintaryhmaOid: "@valintaryhmaOid"}, {
    put: {method: "PUT"}
  });
});



//Hakukohde
app.factory('RootHakukohde', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/hakukohde", {paataso: true}, {
    get: {method: "GET", isArray: true}
  });
});
app.factory('Hakukohde', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/hakukohde/:oid", {oid: "@oid"}, {
    get: {method: "GET"},
    post:{method: "POST"}
  });
});
app.factory('ChildHakukohdes', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/valintaryhma/:oid/hakukohde", {}, {
    get: {method: "GET", isArray: true}
  });
});
app.factory('HakukohdeKuuluuSijoitteluun', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/hakukohde/:oid/kuuluuSijoitteluun", {}, {
    get: {method: "GET"}
  });
});
app.factory('NewHakukohde', function($resource) {
  return $resource(SERVICE_URL_BASE + "resources/hakukohde/", {}, {
    insert: {method: "PUT"}
  });
});
app.factory('HakukohdeValinnanvaihe', function($resource) {
 return $resource(SERVICE_URL_BASE + "resources/hakukohde/:parentOid/valinnanvaihe", {parentOid: "@parentOid"}, {
     get: {method: "GET", isArray: true },
     post:{method: "POST"},
     insert: {method: "PUT"}
   });
});

//Valinnanvaihe
app.factory('Valinnanvaihe', function($resource) {
 return $resource(SERVICE_URL_BASE + "resources/valinnanvaihe/:oid", {oid: "@oid"}, {
     get: {method: "GET"},
     post:{method: "POST"},
     delete: {method: "DELETE"}
   });
});
app.factory('ValinnanvaiheValintatapajono', function($resource) {
 return $resource(SERVICE_URL_BASE + "resources/valinnanvaihe/:parentOid/valintatapajono", {parentOid: "@parentOid"}, {
     get: {method: "GET",  isArray: true},
     insert:{method: "PUT"}
   });
});
app.factory('ValinnanvaiheJarjesta', function($resource) {
    return $resource(SERVICE_URL_BASE + "resources/valinnanvaihe/jarjesta", {}, {
        post: {method: "POST", isArray: true}
    });
});
app.factory('ValinnanvaiheKuuluuSijoitteluun', function($resource) {
return $resource(SERVICE_URL_BASE + "resources/valinnanvaihe/:oid/kuuluuSijoitteluun", {}, {
    get: {method: "GET"}
  });
});



//ValintakoeValinnanvaihe
app.factory('Valintakoe', function($resource) {
  return $resource(SERVICE_URL_BASE + "resources/valinnanvaihe/:valinnanvaiheOid/valintakoe", {valinnanvaiheOid: "@valinnanvaiheOid"}, {
    insert: {method: "PUT"},
    get: {method: "GET", isArray: true}
  });
});




//Valintatapajono
app.factory('Valintatapajono', function($resource) {
 return $resource(SERVICE_URL_BASE + "resources/valintatapajono/:oid", {oid: "@oid"}, {
     get:   {method: "GET"},
     post:  {method: "POST"},
     delete:{method: "DELETE"}
   });
});
app.factory('ValintatapajonoJarjestyskriteeri', function($resource) {
 return $resource(SERVICE_URL_BASE + "resources/valintatapajono/:parentOid/jarjestyskriteeri", {parentOid: "@parentOid"}, {
     get: {method: "GET", isArray: true},
     insert: {method: "PUT"}
   });
});
app.factory('ValintatapajonoJarjesta', function($resource) {
    return $resource(SERVICE_URL_BASE + "resources/valintatapajono/jarjesta", {}, {
        post: {method: "POST", isArray: true}
    });
});



//Järjestyskriteeri
app.factory('Jarjestyskriteeri', function($resource) {
 return $resource(SERVICE_URL_BASE + "resources/jarjestyskriteeri/:oid", {oid: "@oid"}, {
     post: {method: "POST"}
   });
});
app.factory('JarjestyskriteeriJarjesta', function($resource) {
    return $resource(SERVICE_URL_BASE + "resources/jarjestyskriteeri/jarjesta", {}, {
        post: {method: "POST", isArray: true}
    });
});




//Laskentakaava
app.factory('Laskentakaava', function($resource) {
 return $resource(SERVICE_URL_BASE + "resources/laskentakaava/:oid", {oid: "@oid"}, {
     list: {method: "GET", isArray: true},
     get: {method: "GET"},
     insert: {method: "PUT"},
     update: {method: "POST"},
     updateMetadata: {method: "POST", params: {metadata: "true"}}
   });
});

