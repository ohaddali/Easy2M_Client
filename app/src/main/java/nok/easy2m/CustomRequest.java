package nok.easy2m;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by naordalal on 20/02/2018.
 */

public class CustomRequest<T,V> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private final V body;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public CustomRequest(String url, Class<T> clazz, Map<String, String> headers,V body,
                         Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.body = body;
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public byte[] getBody() {
        try {
            return body.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
