import jirareporter.JiraAuthenticationFilter
import jirareporter.UserPasswordEncoderListener
// Place your Spring DSL code here
beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)

    authenticationProcessingFilter(JiraAuthenticationFilter) {
        jiraUserService = ref('jiraUserService')
        authenticationManager = ref('authenticationManager')
        sessionAuthenticationStrategy = ref('sessionAuthenticationStrategy')
        authenticationSuccessHandler = ref('authenticationSuccessHandler')
        authenticationFailureHandler = ref('authenticationFailureHandler')
        rememberMeServices = ref('rememberMeServices')
        authenticationDetailsSource = ref('authenticationDetailsSource')
        requiresAuthenticationRequestMatcher = ref('filterProcessUrlRequestMatcher')
        usernameParameter = 'username'
        passwordParameter = 'password'
        continueChainBeforeSuccessfulAuthentication = false
        allowSessionCreation = true
        postOnly = false
    }
}
