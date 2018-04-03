package br.com.describeplaces.describeplaces

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.os.*
import android.speech.RecognizerIntent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.rekognition.AmazonRekognitionClient
import com.amazonaws.services.rekognition.model.DetectLabelsRequest
import com.amazonaws.services.rekognition.model.Image
import com.google.gson.Gson
import com.microsoft.projectoxford.vision.VisionServiceClient
import com.microsoft.projectoxford.vision.VisionServiceRestClient
import com.microsoft.projectoxford.vision.contract.AnalysisResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.util.*
import java.io.ByteArrayOutputStream
import java.net.URI

class MainActivity  : AppCompatActivity(){

    var URL: String = "https://maps.googleapis.com/maps/api/streetview?size=800x800&location="
    var URL_COMPLETE = "&fov=90&heading=235&pitch=10&key=AIzaSyCazXPoY_JGUEPSDqVmmztG0fISgVlfi5w"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isAudioRecorderGranted()

        button.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            try {
                startActivityForResult(intent, 200)
            } catch (a: ActivityNotFoundException) {
                Toast.makeText(applicationContext, "Intent problem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val imageTask = getImageTask(applicationContext).execute(result[0], URL, URL_COMPLETE)
                //Log.d("CERTO", imageTask.get())
                textView.text = imageTask.get()
                /*var bmp = imageTask.get()
                ivTest.setImageBitmap(bmp)*/
            }
        }
    }

    class getImageTask(private val context: Context): AsyncTask<String, String, String>() {

        val uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze"

        override fun doInBackground(vararg params: String?): String {
            val geocoder = Geocoder(context)
            var addresses = geocoder.getFromLocationName(params[0],1)
            var coordinator = ""
            for (address in addresses) {
                coordinator =
                        address.latitude.toString() + "," + address.longitude.toString()
            }
            var url = params[1]
            url = url + coordinator + params[2]
            /*val credentials = object : AWSCredentials {
                override fun getAWSAccessKeyId(): String {
                    return "AKIAIXZZWKFDOBA4W54Q"
                }

                override fun getAWSSecretKey(): String {
                    return "dh3/UMySXJyQeUmCF0sSn/y2KDxWLFPIYUenCcsY"
                }
            }

            val rekognitionClient = AmazonRekognitionClient(credentials)*/
            var labelResponse = ""
            //url = "https://iqglobal.intel.com/br/wp-content/uploads/sites/15/2016/09/tecnologias-e-esportes-978x653.jpg"
            val bmp = Picasso.get().load(url).get()
            var baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val inputStream = ByteArrayInputStream(baos.toByteArray())
            /*val byteBuffer = ByteBuffer.wrap(baos.toByteArray())
            Image().withBytes(byteBuffer)*/

            /*val request = DetectLabelsRequest()
                    .withImage(Image().withBytes(byteBuffer))
                    .withMaxLabels(10)
                    .withMinConfidence(75f)
            val response = rekognitionClient.detectLabels(request)
            val labels = response.labels

            for (label in labels) {
                labelResponse += label.name
            }*/
            //val ur = "http://wricidades.org/sites/default/files/styles/featured/public/_MG_6312.jpg?itok=R7Ck0crQ"
            val features = arrayOf("ImageType", "Color", "Faces", "Adult", "Categories")
            val details = arrayOf<String>()
            val gson = Gson()
            val client =
                    VisionServiceRestClient(
                            "KEY")

            val v = client.describe(inputStream, 1)
            val result = gson.toJson(v)
            return result
            //return bmp
        }
    }

    fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission is granted", "")
                return true
            } else {

                Log.d("Permission is revoked", "")
                requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.d("Permission is granted", "")
            return true
        }
    }

    fun isAudioRecorderGranted(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    10
            );
            return true
        }
        return false
    }

}
