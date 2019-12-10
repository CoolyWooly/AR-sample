/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.augmentedimage;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

    private static final String TAG = "AugmentedImageNode";

    // The augmented image represented by this node.
    private AugmentedImage image;

    public AugmentedImageNode(Context context) {
        mazeRenderable =
                ModelRenderable.builder()
                    .setSource(context, Uri.parse("Yamato _ Swimwear.sfb"))
//                        .setSource(context, RenderableSource.builder().setSource(
//                                context,
//                                Uri.parse("https://poly.googleusercontent.com/downloads/c/fp/1575802509697169/8MLyqlqG3bR/4UJ5_NVVnUH/sketch.gltf"),
//                                RenderableSource.SourceType.GLTF2).build())
                        .build()
                        .exceptionally(throwable -> {
                    Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    /**
     * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
     * created based on an Anchor created from the image. The corners are then positioned based on the
     * extents of the image. There is no need to worry about world coordinates since everything is
     * relative to the center of the image, which is the parent node of the corners.
     */

    // Add a member variable to hold the maze model.
    private Node mazeNode;

    // Add a variable called mazeRenderable for use with loading
    // GreenMaze.sfb.
    private CompletableFuture<ModelRenderable> mazeRenderable;

    private float maze_scale = 0.0f;

    public void setImage(AugmentedImage image) {
        this.image = image;

        // Initialize mazeNode and set its parents and the Renderable.
        // If any of the models are not loaded, process this function
        // until they all are loaded.
        if (!mazeRenderable.isDone()) {
            CompletableFuture.allOf(mazeRenderable)
                    .thenAccept((Void aVoid) -> setImage(image))
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "Exception loading", throwable);
                                return null;
                            });
            return;
        }

        // Set the anchor based on the center of the image.
        setAnchor(image.createAnchor(image.getCenterPose()));

        mazeNode = new Node();
        mazeNode.setParent(this);
        mazeNode.setWorldScale(new Vector3(0.1f, 0.1f, 0.1f));
        mazeNode.setRenderable(mazeRenderable.getNow(null));


        // Make sure the longest edge fits inside the image.
    /*final float maze_edge_size = 4.65f;
    final float max_image_edge = Math.max(image.getExtentX(), image.getExtentZ());
    maze_scale = max_image_edge / maze_edge_size;

    // Scale Y an extra 10 times to lower the maze wall.
    mazeNode.setLocalScale(new Vector3(maze_scale, maze_scale * 0.6f, maze_scale));*/

    }

    public AugmentedImage getImage() {
        return image;
    }
}
