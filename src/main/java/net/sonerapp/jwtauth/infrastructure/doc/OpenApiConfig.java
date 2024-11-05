package net.sonerapp.jwtauth.infrastructure.doc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import net.sonerapp.jwtauth.infrastructure.security.jwt.JwtUtils;

@OpenAPIDefinition(info = @Info(title = "DB Course Project API", license = @License(name = "MIT License", url = "https://opensource.org/license/mit"), description = "Documentation for all API endpoints of this project", version = "1.0"), servers = {
                @Server(description = "LOCAL ENV", url = "http://localhost:8080") })
@SecurityScheme(name = "accessAuth", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.COOKIE, paramName = JwtUtils.ACCESS_COOKIE_KEY)
@SecurityScheme(name = "refreshAuth", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.COOKIE, paramName = JwtUtils.REFRESH_COOKIE_KEY)
public class OpenApiConfig {

}
