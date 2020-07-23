package fi.vm.sade.service.valintaperusteet.roles;

public class ValintaperusteetRole {
  public static final String READ_UPDATE_CRUD =
      "hasAnyRole('ROLE_APP_VALINTAPERUSTEET_READ','ROLE_APP_VALINTAPERUSTEET_READ_UPDATE','ROLE_APP_VALINTAPERUSTEET_CRUD')";
  public static final String UPDATE_CRUD =
      "hasAnyRole('ROLE_APP_VALINTAPERUSTEET_READ_UPDATE','ROLE_APP_VALINTAPERUSTEET_CRUD')";
  public static final String CRUD = "hasAnyRole('ROLE_APP_VALINTAPERUSTEET_CRUD')";
  public static final String OPH_CRUD =
      "hasAnyRole('ROLE_APP_VALINTAPERUSTEET_CRUD_1.2.246.562.10.00000000001')";
}
