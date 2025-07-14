package codeverse.com.web_be.config.SecurityConfig;

import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final FunctionHelper functionHelper;

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if((StompCommand.CONNECT.equals(accessor.getCommand()))){
            String token = accessor.getFirstNativeHeader("Authorization");

            if(token != null && token.startsWith("Bearer ")){
                token = token.substring(7);

                try{
                    SignedJWT signedJWT = SignedJWT.parse(token);
                    JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

                    if (!signedJWT.verify(verifier)) {
                        throw new RuntimeException("Invalid signature");
                    }

                    JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

                    Date expiration = claims.getExpirationTime();
                    if (expiration.before(new Date())) {
                        throw new RuntimeException("Token expired");
                    }

                    String username = claims.getSubject();

                    User user = functionHelper.getActiveUserByUsername(username);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user.getUsername(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                    );

                    accessor.setUser(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid token: " + e.getMessage());
                }
            }
        }
        return message;
    }
}
