package ir.pr4e.black_apple.utils

import android.content.Context
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class RequestApi(ctx: Context) {
    val context = ctx
    val url = "http://192.168.4.1/cmd"


    fun sendRequest(action: String, data: String, listener: Listener<String>) {
        val queue = Volley.newRequestQueue(context)
        val postRequest: StringRequest = object : StringRequest(
            Method.POST, url, listener,
            Response.ErrorListener { error: VolleyError ->
                    Toast.makeText(
                        context,
                        "Error :$error",
                        Toast.LENGTH_LONG
                    ).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["action"] = action
                params["data"] = data
                return params
            }
        }
        queue.add(postRequest)
    }
}