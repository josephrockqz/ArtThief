package com.example.artthief.ar

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.artthief.R
import com.example.artthief.databinding.ActivityArBinding
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.squareup.picasso.Picasso

class ArActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArBinding

    private lateinit var arFragment: ArFragment

    private lateinit var artworkImageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArBinding.inflate(layoutInflater)
        setContentView(binding.root)

        artworkImageUri =
            intent
                .getStringExtra("artwork_image_url")
                .toString()

        Log.i("howdy", artworkImageUri)

//        Picasso
//            .get()
//            .load(artworkImageUri)
//            .into(findViewById(R.id.iv_augmentedImage))

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            Log.i("howdy", "TAPP")
//            if (plane.type != Plane.Type.VERTICAL) {
//                return@setOnTapArPlaneListener
//            }
            val anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor)
        }
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.parent = anchorNode
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor) {
        ModelRenderable.builder()
            .setSource(this, R.layout.image_augmented)
            .build()
//            .thenAccept {
//                addNodeToScene(fragment, anchor, it)
//            }
    }
}
