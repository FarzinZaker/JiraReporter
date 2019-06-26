package jirareporter

import com.atlassian.jira.rest.client.AuthenticationHandler
import com.atlassian.jira.rest.client.RestClientException
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil
import com.atlassian.jira.rest.client.internal.json.WorklogJsonParser
import com.sun.jersey.api.client.*
import com.sun.jersey.client.apache.ApacheHttpClient
import com.sun.jersey.client.apache.ApacheHttpClientHandler
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject

import javax.ws.rs.core.UriBuilder
import java.util.concurrent.Callable

class JiraRestClient {


    public JiraRestClient(final URI baseUri, final ApacheHttpClient client) {
        this.baseUri = baseUri
        this.client = client
    }

    static class CustomHttpClient extends ApacheHttpClient {

        public CustomHttpClient(ApacheHttpClientHandler handler, AuthenticationHandler authenticationHandler) {
            super(handler)
            this.authenticationHandler = authenticationHandler
        }

        AuthenticationHandler authenticationHandler

        @Override
        public WebResource resource(URI u) {
            final WebResource resource = super.resource(u);
            authenticationHandler.configure(resource, this);
            return resource;
        }

        @Override
        public AsyncWebResource asyncResource(URI u) {
            final AsyncWebResource resource = super.asyncResource(u);
            authenticationHandler.configure(resource, this);
            return resource;
        }

        @Override
        public ViewResource viewResource(URI u) {
            final ViewResource resource = super.viewResource(u);
            authenticationHandler.configure(resource, this);
            return resource;
        }

        @Override
        public AsyncViewResource asyncViewResource(URI u) {
            final AsyncViewResource resource = super.asyncViewResource(u);
            authenticationHandler.configure(resource, this);
            return resource;
        }
    }

    public static ApacheHttpClient getClient(String username, String password) {

        def authenticationHandler = new BasicHttpAuthenticationHandler(username, password)
        DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        authenticationHandler.configure(config);

        return new CustomHttpClient(createDefaultClientHander(config), authenticationHandler)
    }

    private static ApacheHttpClientHandler createDefaultClientHander(DefaultApacheHttpClientConfig config) {
        final HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        return new ApacheHttpClientHandler(client, config);
    }

    def getURL(String issueUri) {
        final URI roleUri = UriBuilder
                .fromUri(new URI(issueUri))
                .build()
        return getAndParse(roleUri)
    }

    def getURLAsList(String issueUri) {
        final URI roleUri = UriBuilder
                .fromUri(new URI(issueUri))
                .build()
        return getAndParseList(roleUri)
    }

    protected final ApacheHttpClient client;
    protected final URI baseUri;


    protected JSONObject invoke(Callable<JSONObject> callable) throws RestClientException {
        try {
            callable.call();
        } catch (UniformInterfaceException e) {

            try {
                final String body = e.getResponse().getEntity(String.class);
                final Collection<String> errorMessages = extractErrors(body);
                throw new RestClientException(errorMessages, e);
            } catch (JSONException e1) {
                throw new RestClientException(e);
            }
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RestClientException(e);
        }
    }
    protected JSONArray invokeArray(Callable<JSONArray> callable) throws RestClientException {
        try {
            callable.call();
        } catch (UniformInterfaceException e) {

            try {
                final String body = e.getResponse().getEntity(String.class);
                final Collection<String> errorMessages = extractErrors(body);
                throw new RestClientException(errorMessages, e);
            } catch (JSONException e1) {
                throw new RestClientException(e);
            }
        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RestClientException(e);
        }
    }

    protected JSONObject getAndParse(final URI uri) {
        invoke(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                final WebResource webResource = client.resource(uri)
                webResource.get(JSONObject.class)
            }
        });
    }

    protected JSONArray getAndParseList(final URI uri) {
        invokeArray(new Callable<JSONArray>() {
            @Override
            public JSONArray call() throws Exception {
                final WebResource webResource = client.resource(uri)
                webResource.get(JSONArray.class)
            }
        });
    }


    static Collection<String> extractErrors(String body) throws JSONException {
        JSONObject jsonObject = new JSONObject(body);
        final Collection<String> errorMessages = new ArrayList<String>();
        final JSONArray errorMessagesJsonArray = jsonObject.optJSONArray("errorMessages");
        if (errorMessagesJsonArray != null) {
            errorMessages.addAll(JsonParseUtil.toStringCollection(errorMessagesJsonArray));
        }
        final JSONObject errorJsonObject = jsonObject.optJSONObject("errors");
        if (errorJsonObject != null) {
            final JSONArray valuesJsonArray = errorJsonObject.toJSONArray(errorJsonObject.names());
            if (valuesJsonArray != null) {
                errorMessages.addAll(JsonParseUtil.toStringCollection(valuesJsonArray));
            }
        }
        return errorMessages;
    }


}
