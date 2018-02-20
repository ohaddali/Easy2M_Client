package nok.easy2m.communityLayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
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
        this.domain = "http://10.0.0.16"; //TODO: Change with domain
        queue = Volley.newRequestQueue(context);
    }

    /**
     *Add JsonObjectRequest to RequestQueue
     @param serviceName the service name to call
     @param methodName the method name to call
     @param responseCallBack call back that except the response of request
     @param errorResponseCallBack call back that except the error of request
     @param params params that send on the body of the post request , each param is of the form Pair(String,Value) or Pair(String,JsonObject)
     */
    public <T> void send(String serviceName , String methodName , final @NotNull CallBack<T> responseCallBack,
            Class<T> classType, final @Nullable CallBack<VolleyError> errorResponseCallBack , Pair<String,Object>... params)
    {
        JSONObject param = getParamAsJsonFromArrayOfParams(params);
        serviceName = (serviceName.contains("svc")) ? serviceName : serviceName + ".svc";
        String url = domain + "/" + serviceName + "/" + methodName;
        HashMap<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json; charset=utf-8");

        CustomRequest<T,JSONObject> customRequest = new CustomRequest<>(url, classType , headers , param,
                response -> responseCallBack.execute(response),
                error -> {
                    if(errorResponseCallBack != null)
                        errorResponseCallBack.execute(error);
                });


        // Add the request to the RequestQueue.
        queue.add(customRequest);
    }

    private JSONObject getParamAsJsonFromArrayOfParams(Pair<String,Object>[] params)
    {
        Map<String,String> json = new HashMap<>();
        for(Pair<String,Object> param : params)
        {
            json.put(param.first , param.second.toString());
        }

        return new JSONObject(json);
    }

    private Map<String,String> getParamAsMapFromArrayOfParams(Pair<String,Object>[] params)
    {
        Map<String,String> json = new HashMap<>();
        for(Pair<String,Object> param : params)
        {
            json.put(param.first , param.second.toString());
        }

        return json;
    }


}
