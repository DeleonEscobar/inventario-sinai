package sv.sinai.server.utils.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sv.sinai.server.services.security.JwtService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Inyectar las dependencias necesarias
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Constructor para inicializar las dependencias necesarias
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Metodo para filtrar las peticiones entrantes y autenticar al usuario
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = getTokenRequest(request); // Obtener el token de la petición
        final String username; // Variable para almacenar el nombre de usuario

        try {
            if (token == null) { // Si el token es nulo, continuar con el siguiente filtro
                filterChain.doFilter(request, response);
                return;
            }

            // Extraer el nombre de usuario del token
            username = jwtService.getUsernameFromToken(token);

            // Si el nombre de usuario no es nulo pero el contexto de autenticación sí es nulo, cargar el usuario
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Si el token es válido, establecer el token de autenticación
                if(jwtService.isValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Establecer el contexto de seguridad con el token de autenticación
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        // Manejo de excepciones según el caso
        } catch (ExpiredJwtException e) {
            handleExceptionResponse(response, "Token expired", HttpStatus.UNAUTHORIZED);
            return;
        } catch (UnsupportedJwtException e) {
            handleExceptionResponse(response, "Unsupported JWT token", HttpStatus.UNAUTHORIZED);
            return;
        } catch (MalformedJwtException e) {
            handleExceptionResponse(response, "Malformed JWT token", HttpStatus.UNAUTHORIZED);
            return;
        } catch (SignatureException e) {
            handleExceptionResponse(response, "Invalid JWT signature", HttpStatus.UNAUTHORIZED);
            return;
        } catch (IllegalArgumentException e) {
            handleExceptionResponse(response, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            return;
        }

        // Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }

    private String getTokenRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    // Metodo extra para manejar excepciones y enviar respuestas JSON
    private void handleExceptionResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
