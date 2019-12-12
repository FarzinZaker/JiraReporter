package jirareporter

import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.util.Assert

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * Created by root on 6/10/17.
 */
class JiraAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    JiraUserService jiraUserService

    @Override
    Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        def authentication
        try {
            authentication = super.attemptAuthentication(request, response)
        } catch (ex) {
            def authenticationResult = jiraUserService.authenticate(obtainUsername(request), obtainPassword(request))
            authentication = new UsernamePasswordAuthenticationToken(authenticationResult.principal, null, authenticationResult.authorities)
        }

//        println authentication
        authentication
    }
}