package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SimpleGame extends Game implements InputProcessor {
	final public static int virtualWidth = 640;
	final public static int virtualHeight = 600;

	AssetManager assetManager;
	BitmapFont loadingFont;
	float soundVolume;
	boolean isMusicON;

	@Override
	public void create() {
		soundVolume = 0.05f;
		isMusicON = true;

		assetManager = new AssetManager();

		assetManager.load("Bat.png", Texture.class);
		assetManager.load("BatHit.png", Texture.class);
		assetManager.load("Chicken.png", Texture.class);
		assetManager.load("ChickenHit.png", Texture.class);
		assetManager.load("Pig.png", Texture.class);
		assetManager.load("PigAngry.png", Texture.class);
		assetManager.load("PigHit.png", Texture.class);
		assetManager.load("Snail.png", Texture.class);
		assetManager.load("SnailHit.png", Texture.class);
		assetManager.load("kleeIdle.png", Texture.class);
		assetManager.load("kleeRun.png", Texture.class);
		assetManager.load("kleeBomb.png", Texture.class);
		assetManager.load("kleeBombBoom.png", Texture.class);
		assetManager.load("kleeLose.png", Texture.class);
		assetManager.load("kleeWin.png", Texture.class);
		assetManager.load("skill_E.png", Texture.class);
		assetManager.load("bg.png", Texture.class);
		assetManager.load("win_bg.png", Texture.class);
		assetManager.load("lose_bg.png", Texture.class);
		assetManager.load("Line.png", Texture.class);
		assetManager.load("kleeMM_idle.png", Texture.class);
		assetManager.load("button_Click.png", Texture.class);
		assetManager.load("button_E.png", Texture.class);
		assetManager.load("button_Q.png", Texture.class);
		assetManager.load("gameUI.png", Texture.class);
		assetManager.load("buttonEMask.png", Texture.class);
		assetManager.load("buttonQMask.png", Texture.class);
		assetManager.load("blank.png", Texture.class);

		assetManager.load("bgm.mp3", Music.class);

		assetManager.load("collect.wav", Sound.class);
		assetManager.load("explode1.wav", Sound.class);
		assetManager.load("explode2.wav", Sound.class);
		assetManager.load("explode3.wav", Sound.class);
		assetManager.load("explode4.wav", Sound.class);
		assetManager.load("pressQ.wav", Sound.class);


		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("sysFont.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 20;
		parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
		parameter.flip = true;
		loadingFont = generator.generateFont(parameter);
		generator.dispose();

		FileHandleResolver resolver = new InternalFileHandleResolver();
		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

		// UI Font

		FreetypeFontLoader.FreeTypeFontLoaderParameter uiFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		uiFont.fontFileName = "font.ttf";
		uiFont.fontParameters.size = 16;
		assetManager.load("uiFont.ttf", BitmapFont.class, uiFont);

		// Small Font

		FreetypeFontLoader.FreeTypeFontLoaderParameter mySmallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		mySmallFont.fontFileName = "font.ttf";
		mySmallFont.fontParameters.size = 22;
		assetManager.load("smallfont.ttf", BitmapFont.class, mySmallFont);

		// Big Font

		FreetypeFontLoader.FreeTypeFontLoaderParameter myBigFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		myBigFont.fontFileName = "font.ttf";
		myBigFont.fontParameters.size = 44;
		assetManager.load("bigfont.ttf", BitmapFont.class, myBigFont);

		SkinLoader.SkinParameter skinParam = new SkinLoader.SkinParameter("uiskin.atlas");
		assetManager.load("uiskin.json", Skin.class, skinParam);

		this.setScreen(new LoadingScreen(this));
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public BitmapFont getLoadingFont() {
		return loadingFont;
	}

	public float getSoundVolume() {
		return soundVolume;
	}

	public void setSoundVolume(float soundVolume) {
		this.soundVolume = soundVolume;
	}

	public boolean isMusicON() {
		return isMusicON;
	}

	public void setMusicON(boolean musicON) {
		isMusicON = musicON;
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		loadingFont.dispose();
		screen.dispose();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public boolean keyDown(int i) {
		return false;
	}

	@Override
	public boolean keyUp(int i) {
		return false;
	}

	@Override
	public boolean keyTyped(char c) {
		return false;
	}

	@Override
	public boolean touchDown(int i, int i1, int i2, int i3) {
		return false;
	}

	@Override
	public boolean touchUp(int i, int i1, int i2, int i3) {
		return false;
	}

	@Override
	public boolean touchDragged(int i, int i1, int i2) {
		return false;
	}

	@Override
	public boolean mouseMoved(int i, int i1) {
		return false;
	}

	@Override
	public boolean scrolled(float v, float v1) {
		return false;
	}
}
