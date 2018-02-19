package nok.easy2m;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by naordalal on 19/02/2018.
 */

public class HttpConnection
{
    public static volatile HttpConnection instance;
    private final Context context;
    private RequestQueue queue;
    private final String domain;

    public static synchronized HttpConnection getInstance(Context context) {
        if (instance == null) {
            instance = new HttpConnection(context);
        }
        return instance;
    }

    //private constructor.
    private HttpConnection(Context context)
    {
        this.context = context;
        this.domain = ""; //TODO: Change with domain
    }

    /**
     *Add JsonObjectRequest to RequestQueue
     @param serviceName the service name to call
     @param methodName the method name to call
     @param responseCallBack call back that except the response of request
     @param errorResponseCallBack call back that except the error of request
     @param params params that send on the body of the post request , each param is of the form Pair(String,Value) or Pair(String,Map(String,Value))
     */
    public void send(String serviceName , String methodName , final @NotNull CallBack<JSONObject> responseCallBack
            , final @Nullable CallBack<VolleyError> errorResponseCallBack , Pair<String,Object>... params)
    {
        JSONObject param = getParamFromArrayOfParams(params);
        serviceName = (serviceName.contains("svc")) ? serviceName : serviceName + ".svc";
        String url = domain + "/" + serviceName + "/" + methodName;
        queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        responseCallBack.execute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if(errorResponseCallBack != null)
                            errorResponseCallBack.execute(error);
                    }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private JSONObject getParamFromArrayOfParams(Pair<String,Object>[] params)
    {
        Map<String,String> json = new HashMap<>();
        for(Pair<String,Object> param : params)
        {
            if(param.second instanceof Map)
                json.put(param.first , new JSONObject((Map<String,String>)param.second).toString());
            else
                json.put(param.first , param.second.toString());
        }

        return new JSONObject(json);
    }


}
