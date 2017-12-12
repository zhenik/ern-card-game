package no.ern.game.api.filter

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureException
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val SECRET_KEY = "secretkey"

class JwtFilter : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest,
                          res: ServletResponse,
                          chain: FilterChain) {
        val request = req as HttpServletRequest
        val response = res as HttpServletResponse
        val authHeader = request.getHeader("Authorization")

        if ("OPTION" == request.method) {
            response.status = HttpServletResponse.SC_OK

            chain.doFilter(req, res)
        } else {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw ServletException("Missing or invalid Authorization header")
            }

            val token = authHeader.substring(7)
            println(token)
            try {
                // secretKey -> change it in future on smth strong and dif to bruteForce
                val claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body
                // see claim eg. ({sub=yohoho, roles=user, iat=1509797567})
                println(claims)
                request.setAttribute("claims", claims)
            } catch (e: SignatureException) {
                throw ServletException("Invalid token")
            }

            // here we also need filter
            chain.doFilter(req, res)
        }
    }
}