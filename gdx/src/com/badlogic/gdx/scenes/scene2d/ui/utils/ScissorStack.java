package com.badlogic.gdx.scenes.scene2d.ui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ScissorStack {
	private static Array<Rectangle> scissors = new Array<Rectangle>();
	
	public static void pushScissors(Rectangle scissor) {				
		if(scissors.size == 0) {
			Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);		
		}
		else {
			 //merge scissors
			Rectangle parent = scissors.get(scissors.size-1);
			float minX = Math.max(parent.x, scissor.x);
			float maxX = Math.min(parent.x + parent.width, scissor.x + scissor.width);
			scissor.x = minX;
			scissor.width = maxX - minX;
			
			float minY = Math.max(parent.y, scissor.y);
			float maxY = Math.min(parent.y + parent.height, scissor.y + scissor.height);
			scissor.y = minY;
			scissor.height = maxY - minY;
		}		
		scissors.add(scissor);		
		Gdx.gl.glScissor((int)scissor.x, (int)scissor.y, (int)scissor.width, (int)scissor.height);
	}
	
	public static void popScissors() {
		scissors.pop();		
		if(scissors.size == 0) Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
		else {
			Rectangle scissor = scissors.peek();
			Gdx.gl.glScissor((int)scissor.x, (int)scissor.y, (int)scissor.width, (int)scissor.height);
		}
	}
	
	static Vector3 tmp = new Vector3(); 
	public static void calculateScissors(Camera camera, Matrix4 batchTransform, Rectangle area, Rectangle scissor) {
		tmp.set(area.x, area.y, 0);
		tmp.mul(batchTransform);
		camera.project(tmp);		
		scissor.x = tmp.x;
		scissor.y = tmp.y;
		
		tmp.set(area.x + area.width, area.y + area.height, 0);
		tmp.mul(batchTransform);
		camera.project(tmp);		
		scissor.width = tmp.x - scissor.x;
		scissor.height = tmp.y - scissor.y;		
	}

	static final Rectangle viewport = new Rectangle();
	public static Rectangle getViewport () {
		if(scissors.size == 0) {
			viewport.set(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			return viewport;
		} else {
			Rectangle scissor = scissors.peek();
			viewport.set(scissor);
			return viewport;
		}		
	}

	public static void toWindowCoordinates (Camera camera, Matrix4 transformMatrix, Vector2 point) {
		tmp.set(point.x, point.y, 0);
		tmp.mul(transformMatrix);
		camera.project(tmp);
		tmp.y = Gdx.graphics.getHeight() - tmp.y;
		point.x = tmp.x;
		point.y = tmp.y;
	}
}