package com.nttdata.recetas;

import com.nttdata.recetas.model.ConfigResponse;
import com.nttdata.recetas.model.FirmaRequest;
import com.nttdata.recetas.model.FirmaResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * API de recetas. Los tres secretos deben llegar desde HashiCorp Vault como
 * propiedades: el nombre de cada @ConfigProperty tiene que coincidir EXACTAMENTE
 * con la key del secreto  secret/clinica/ms-recetas.
 */
// TODO 5: anota la clase con @Path("/api/recetas")
    @Path("/api/recetas")
public class RecetaResource {

    // TODO 6: inyecta la api-key de la farmacia con @ConfigProperty.
    //         El nombre debe ser la key exacta del secreto en Vault.
    @ConfigProperty(name = "farmacia.api-key")
    String apiKey;

    // TODO 7: inyecta la URL de la farmacia.
    @ConfigProperty(name = "farmacia.url")
    String farmaciaUrl;

    // TODO 8: inyecta la clave de firma.
    @ConfigProperty(name = "firma.clave")
    String firmaClave;

    private final FirmaService firmaService;

    public RecetaResource(FirmaService firmaService) {
        this.firmaService = firmaService;
    }

    // TODO 9: anota con @GET, @Path("/config") y @Produces(MediaType.APPLICATION_JSON)
    // TODO 10 (RN-03): devuelve un ConfigResponse con la URL de la farmacia,
    //          la api-key ENMASCARADA y si la clave de firma esta configurada.
    @GET
    @Path("/config")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigResponse config() {

        return new ConfigResponse(farmaciaUrl, enmascarar(apiKey),
                firmaClave != null && !firmaClave.isBlank());
    }

    // TODO 11: anota con @POST, @Path("/firmar") y @Produces(MediaType.APPLICATION_JSON)
    // TODO 12 (RN-05 y RN-04): si el codigo de receta es nulo o esta en blanco devuelve 400;
    //          si no, firma la receta con firmaService y devuelve 200 con un FirmaResponse.
    @POST
    @Path("/firmar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response firmar(FirmaRequest request) {
        if (request == null || request.codigoReceta() == null || request.codigoReceta().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String firma = firmaService.firmar(request.codigoReceta(), firmaClave);

        return Response.ok(new FirmaResponse(
                request.codigoReceta(),
                request.paciente(),
                firma
        )).build();
    }

    // TODO 15 (RN-03): devuelve "****" seguido de los ultimos 4 caracteres del valor.
    //          Si el valor es nulo o tiene menos de 4 caracteres, devuelve solo "****".
    String enmascarar(String valor) {
        if (valor == null || valor.length() < 4) {
            return "****";
        }
        return "****" + valor.substring(valor.length() - 4);
    }
}
