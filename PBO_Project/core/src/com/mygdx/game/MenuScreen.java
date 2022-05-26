package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen, InputProcessor {
    Game parentGame;
    AssetManager assetManager;
    SpriteBatch batch;
    OrthographicCamera camera, stageCamera;
    Viewport viewport;
    Music music;
    KleeMainMenu klee;

    Stage stage;
    Label titleLabel, optionSoundLabel, optionMusicLabel, playDescLabel, aboutLabel, creditLabel, creditLink1, creditLink2, creditLink3;
    TextButton playButton, optionButton, optionDoneButton, level1Button, level2Button, level3Button, level4Button, level5Button, playBackButton, aboutButton, aboutBackButton, creditButton, creditBackButton, exitButton;
    Window optionWindow, playWindow, aboutWindow, creditWindow;
    CheckBox musicCheckbox, soundCheckbox;

    InputMultiplexer multiInput;

    public MenuScreen(Game g) {
        parentGame = g;
        Initialize();
    }

    protected void Initialize() {
        assetManager = ((SimpleGame) parentGame).getAssetManager();
        camera = new OrthographicCamera(SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        camera.setToOrtho(false, SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        viewport = new FitViewport(SimpleGame.virtualWidth, SimpleGame.virtualHeight, camera);
        batch = new SpriteBatch();

        multiInput = new InputMultiplexer();
        multiInput.addProcessor(this);

        stageCamera = new OrthographicCamera(SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        stageCamera.setToOrtho(false, SimpleGame.virtualWidth, SimpleGame.virtualHeight);
        stage = new Stage(new FitViewport(SimpleGame.virtualWidth, SimpleGame.virtualHeight, stageCamera));

        multiInput.addProcessor(stage);

        Skin mySkin = assetManager.get("uiskin.json", Skin.class);

        titleLabel = new Label("Klee's Emergency\nDefense System!\n(AI Edition)", mySkin);
        Label.LabelStyle style = new Label.LabelStyle(titleLabel.getStyle());
        style.font = assetManager.get("bigfont.ttf", BitmapFont.class);
        titleLabel.setStyle(style);
        titleLabel.setWidth(640);
        titleLabel.setPosition(0, 440);
        titleLabel.setAlignment(Align.center);
        titleLabel.setColor(Color.BLACK);
        stage.addActor(titleLabel);

        playButton = new TextButton("Play", mySkin);
        playButton.setSize(180, 64);
        playButton.setPosition(320 - playButton.getWidth() / 2, 280);
        playButton.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer,  Actor fromActor) {
                klee.setX(playButton.getX() - 60);
                klee.setY(playButton.getY() + playButton.getHeight() / 2);
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    playWindow.setVisible(true);
                    playDescLabel.setText("Hover over a difficulty option to learn about it!");
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(playButton);

        optionButton = new TextButton("Settings", mySkin);
        optionButton.setSize(150, 48);
        optionButton.setPosition(320 - optionButton.getWidth() / 2, 200);
        optionButton.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer,  Actor fromActor) {
                klee.setX(optionButton.getX() - 60);
                klee.setY(optionButton.getY() + optionButton.getHeight() / 2);
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if(x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    optionWindow.setVisible(true);
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(optionButton);

        exitButton = new TextButton("Exit", mySkin);
        exitButton.setSize(150, 48);
        exitButton.setPosition(320 - exitButton.getWidth() / 2, 140);
        exitButton.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer,  Actor fromActor) {
                klee.setX(exitButton.getX() - 60);
                klee.setY(exitButton.getY() + exitButton.getHeight() / 2);
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if(x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    Gdx.app.exit();
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(exitButton);

        optionWindow = new Window("Settings", mySkin);
        optionWindow.setSize(480, 320);
        optionWindow.setPosition(320 - optionWindow.getWidth() / 2, 300 - optionWindow.getHeight() / 2);
        optionWindow.setMovable(false);
        optionWindow.setModal(true);
        optionWindow.setResizable(false);
        optionWindow.setVisible(false);
        optionWindow.getTitleLabel().setAlignment(Align.center);
        stage.addActor(optionWindow);

        optionSoundLabel = new Label("Sound :", mySkin);
        optionSoundLabel.setAlignment(Align.right);
        optionSoundLabel.setY(250);
        optionSoundLabel.setX(0);
        optionSoundLabel.setWidth(optionWindow.getWidth() / 2);
        optionWindow.addActor(optionSoundLabel);

        optionMusicLabel = new Label("Music :", mySkin);
        optionMusicLabel.setAlignment(Align.right);
        optionMusicLabel.setY(225);
        optionMusicLabel.setX(0);
        optionMusicLabel.setWidth(optionWindow.getWidth() / 2);
        optionWindow.addActor(optionMusicLabel);

        soundCheckbox = new CheckBox("", mySkin);
        soundCheckbox.setY(250);
        soundCheckbox.setX(optionWindow.getWidth() / 2 + soundCheckbox.getWidth());
        if (((SimpleGame) parentGame).getSoundVolume() > 0) {
            soundCheckbox.setChecked(true);
        }
        else if (((SimpleGame) parentGame).getSoundVolume() == 0) {
            soundCheckbox.setChecked(false);
        }
        soundCheckbox.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (soundCheckbox.isChecked())
                    ((SimpleGame) parentGame).setSoundVolume(0.05f);
                else
                    ((SimpleGame) parentGame).setSoundVolume(0);
            }
        });
        optionWindow.addActor(soundCheckbox);

        musicCheckbox = new CheckBox("", mySkin);
        musicCheckbox.setY(225);
        musicCheckbox.setX(optionWindow.getWidth() / 2 + musicCheckbox.getWidth());
        musicCheckbox.setChecked(((SimpleGame) parentGame).isMusicON());
        musicCheckbox.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (musicCheckbox.isChecked()) {
                    ((SimpleGame) parentGame).setMusicON(true);
                    music.play();
                }
                else {
                    ((SimpleGame) parentGame).setMusicON(false);
                    music.stop();
                }
            }
        });
        optionWindow.addActor(musicCheckbox);

        optionDoneButton = new TextButton("Back",mySkin);
        optionDoneButton.setSize(75, 24);
        optionDoneButton.setPosition(optionWindow.getWidth() / 2 - optionDoneButton.getWidth() / 2,20);
        optionDoneButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if(x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    optionWindow.setVisible(false);
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        optionWindow.addActor(optionDoneButton);

        playWindow = new Window("Select Difficulty", mySkin);
        playWindow.setSize(540, 480);
        playWindow.setPosition(320 - playWindow.getWidth() / 2, 300 - playWindow.getHeight() / 2);
        playWindow.setMovable(false);
        playWindow.setModal(true);
        playWindow.setResizable(false);
        playWindow.setVisible(false);
        playWindow.getTitleLabel().setAlignment(Align.center);
        stage.addActor(playWindow);

        playDescLabel = new Label("Hover over a difficulty option to learn about it!", mySkin);
        playDescLabel.setAlignment(Align.center);
        playDescLabel.setPosition(0, 100);
        playDescLabel.setWidth(playWindow.getWidth());
        playWindow.addActor(playDescLabel);

        playBackButton = new TextButton("Back", mySkin);
        playBackButton.setSize(75, 24);
        playBackButton.setPosition(playWindow.getWidth() / 2 - playBackButton.getWidth() / 2, 20);
        playBackButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    playWindow.setVisible(false);
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        playWindow.addActor(playBackButton);

        level1Button = new TextButton("Easy", mySkin);
        level1Button.setSize(150, 36);
        level1Button.setPosition(playWindow.getWidth() / 2 - level1Button.getWidth() / 2, playWindow.getHeight() - 80);
        level1Button.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
                playDescLabel.setText("For beginners!\n\nScore Multiplier: 0.5x");
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
                playDescLabel.setText("Hover over a difficulty option to learn about it!");
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    parentGame.setScreen(new GameScreen(parentGame, 1));
                    MenuScreen.this.dispose();
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        playWindow.addActor(level1Button);

        level2Button = new TextButton("Normal", mySkin);
        level2Button.setSize(150, 36);
        level2Button.setPosition(playWindow.getWidth() / 2 - level2Button.getWidth() / 2, playWindow.getHeight() - 126);
        level2Button.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer,  Actor fromActor) {
                playDescLabel.setText("The standard experience!\n\nScore Multiplier: 1.0x");
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer,  Actor toActor) {
                playDescLabel.setText("Hover over a difficulty option to learn about it!");
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    parentGame.setScreen(new GameScreen(parentGame, 2));
                    MenuScreen.this.dispose();
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        playWindow.addActor(level2Button);

        level3Button = new TextButton("Hard", mySkin);
        level3Button.setSize(150, 36);
        level3Button.setPosition(playWindow.getWidth() / 2 - level3Button.getWidth() / 2, playWindow.getHeight() - 172);
        level3Button.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer,  Actor fromActor) {
                playDescLabel.setText("For the challenge seekers!\nIt's quite cloudy today, beware of strong winds!\n\nFollowing her mother's advice, Klee decided to carry some\nspecially crafted explosives in response to the weather!\nScore Multiplier: 1.5x");
                playDescLabel.setY(110);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer,  Actor toActor) {
                playDescLabel.setText("Hover over a difficulty option to learn about it!");
                playDescLabel.setY(100);
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    parentGame.setScreen(new GameScreen(parentGame, 3));
                    MenuScreen.this.dispose();
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        playWindow.addActor(level3Button);

        level4Button = new TextButton("Insane", mySkin);
        level4Button.setSize(150, 36);
        level4Button.setPosition(playWindow.getWidth() / 2 - level4Button.getWidth() / 2, playWindow.getHeight() - 218);
        level4Button.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer,  Actor fromActor) {
                playDescLabel.setText("When a hard challenge is simply not enough!\nThe weather seems bad today, watch out for\nstrong winds and stray thunderbolts!\n\nIn response to the harsh conditions, Klee decided\nto carry even more of her special gunpowder formula!\n\nScore Multiplier: 2.0x");
                playDescLabel.setY(110);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer,  Actor toActor) {
                playDescLabel.setText("Hover over a difficulty option to learn about it!");
                playDescLabel.setY(100);
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    parentGame.setScreen(new GameScreen(parentGame, 4));
                    MenuScreen.this.dispose();
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        playWindow.addActor(level4Button);

        level5Button = new TextButton("Frenzy", mySkin);
        level5Button.setSize(150, 36);
        level5Button.setPosition(playWindow.getWidth() / 2 - level5Button.getWidth() / 2, playWindow.getHeight() - 264);
        level5Button.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer,  Actor fromActor) {
                playDescLabel.setText("When you simply want to obliterate a bunch of enemies without\nworrying about your safety!\nKlee is invincible, always moves faster than the fastest enemy\nand her abilities grant her godlike powers!\n\nScore Multiplier: 3.0x");
                playDescLabel.setY(110);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer,  Actor toActor) {
                playDescLabel.setText("Hover over a difficulty option to learn about it!");
                playDescLabel.setY(100);
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    parentGame.setScreen(new GameScreen(parentGame, 5));
                    MenuScreen.this.dispose();
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        playWindow.addActor(level5Button);

        aboutWindow = new Window("About", mySkin);
        aboutWindow.setSize(480, 320);
        aboutWindow.setPosition(320 - aboutWindow.getWidth() / 2, 300 - aboutWindow.getHeight() / 2);
        aboutWindow.setMovable(false);
        aboutWindow.setModal(true);
        aboutWindow.setResizable(false);
        aboutWindow.setVisible(false);
        aboutWindow.getTitleLabel().setAlignment(Align.center);
        stage.addActor(aboutWindow);

        aboutLabel = new Label("Klee's Emergency Defense System! (AI Edition)\n\nProyek KB\n\nKelompok 5\nDenzel Daniel D'Assante Tangsaputra - C14200160\nJustin A. H. Rampengan - C14200148\nWendy L. Paath - C14200150\nBrigitta A. Heryanto - C1400188", mySkin);
        aboutLabel.setPosition(aboutWindow.getWidth() / 2 - aboutLabel.getWidth() / 2, 80);
        aboutLabel.setAlignment(Align.center);
        aboutWindow.addActor(aboutLabel);

        aboutButton = new TextButton("About", mySkin);
        aboutButton.setSize(75, 24);
        aboutButton.setPosition(20, 20);
        aboutButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    aboutWindow.setVisible(true);
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(aboutButton);

        aboutBackButton = new TextButton("Back", mySkin);
        aboutBackButton.setSize(75, 24);
        aboutBackButton.setPosition(aboutWindow.getWidth() / 2 - aboutBackButton.getWidth() / 2, 20);
        aboutBackButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    aboutWindow.setVisible(false);
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        aboutWindow.addActor(aboutBackButton);

        creditButton = new TextButton("Credit", mySkin);
        creditButton.setSize(75, 24);
        creditButton.setPosition(620 - creditButton.getWidth(), 20);
        creditButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    creditWindow.setVisible(true);
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(creditButton);

        creditWindow = new Window("Credit", mySkin);
        creditWindow.setSize(540, 400);
        creditWindow.setPosition(320 - creditWindow.getWidth() / 2, 300 - creditWindow.getHeight() / 2);
        creditWindow.setMovable(false);
        creditWindow.setModal(true);
        creditWindow.setResizable(false);
        creditWindow.setVisible(false);
        creditWindow.getTitleLabel().setAlignment(Align.center);
        stage.addActor(creditWindow);

        creditLabel = new Label("Klee, Genshin Impact (c) HoYoverse\nFonts & gameplay sound effects extracted from Genshin Impact files\n\n\nKlee Sprites by @uuteki_art on Twitter\n\n\nEnemy sprites by Pixel Frog\n\n\nMisc. sprite edits by Denzel Daniel D.T.\n\n\n\n\n(Click on links to open in browser)", mySkin);
        creditLabel.setPosition(creditWindow.getWidth() / 2 - creditLabel.getWidth() / 2, 50);
        creditLabel.setAlignment(Align.center);
        creditWindow.addActor(creditLabel);

        creditLink1 = new Label("https://genshin.hoyoverse.com/en/home", mySkin);
        creditLink1.setPosition(creditWindow.getWidth() / 2 - creditLink1.getWidth() / 2, 310);
        creditLink1.setAlignment(Align.center);
        creditLink1.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    Gdx.net.openURI("https://genshin.hoyoverse.com/en/home");
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        creditWindow.addActor(creditLink1);

        creditLink2 = new Label("https://twitter.com/uuteki_art/status/1401281913692827659", mySkin);
        creditLink2.setPosition(creditWindow.getWidth() / 2 - creditLink2.getWidth() / 2, 250);
        creditLink2.setAlignment(Align.center);
        creditLink2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    Gdx.net.openURI("https://twitter.com/uuteki_art/status/1401281913692827659");
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        creditWindow.addActor(creditLink2);

        creditLink3 = new Label("https://pixelfrog-assets.itch.io/", mySkin);
        creditLink3.setPosition(creditWindow.getWidth() / 2 - creditLink3.getWidth() / 2, 190);
        creditLink3.setAlignment(Align.center);
        creditLink3.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    Gdx.net.openURI("https://pixelfrog-assets.itch.io/");
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        creditWindow.addActor(creditLink3);

        creditBackButton = new TextButton("Back", mySkin);
        creditBackButton.setSize(75, 24);
        creditBackButton.setPosition(creditWindow.getWidth() / 2 - creditBackButton.getWidth() / 2,20);
        creditBackButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (x >= 0 && y >= 0 && x <= event.getTarget().getWidth() && y <= event.getTarget().getHeight()) {
                    creditWindow.setVisible(false);
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        creditWindow.addActor(creditBackButton);

        klee = new KleeMainMenu(playButton.getX() - 60, 600 - playButton.getY() - playButton.getHeight() / 2);
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiInput);
        music = assetManager.get("bgm.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.05f);
        if (!music.isPlaying() && ((SimpleGame) parentGame).isMusicON())
            music.play();
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        draw();
        batch.end();
        update();
        stage.act();
        stage.draw();
    }

    public void draw() {
        Texture background = assetManager.get("bg.png", Texture.class);
        batch.draw(background, 0, 0);

        klee.draw(batch);
    }

    public void update() {
        klee.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        System.out.println("MenuScreen Disposed");
    }
}
