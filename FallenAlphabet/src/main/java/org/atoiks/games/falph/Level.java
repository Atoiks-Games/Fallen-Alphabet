/**
 *  Fallen Alphabet
 *  Copyright (C) 2017-2018  Atoiks-Games <atoiks-games@outlook.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.atoiks.games.falph;

import java.awt.Color;
import java.awt.event.KeyEvent;

import java.util.Random;

import org.atoiks.games.framework2d.IGraphics;
import org.atoiks.games.framework2d.GameScene;

import org.atoiks.games.staventure.colliders.*;

public class Level extends GameScene {

    public enum State {
        ZOMBIE, JUMP_1, JUMP_2;
    }

    public static final int RADIUS = 10;
    public static final float BASE_LINE = 440;

    public static final float JUMP_SPEED = 50f;
    public static final float Y_SCROLL = 19.6f;
    public static final float X_SCROLL = 19.6f;

    private final Random rnd = new Random();

    private final CircleCollider ball = new CircleCollider(0, 0, RADIUS);

    private float dx;
    private float dy;

    private State state;

    private Collider[] group;

    private boolean penalty;

    @Override
    public void enter(int from) {
        penalty = false;

        group = new Collider[50];
        float lastX = 200;
        float lastW = 0;
        for (int i = 0; i < group.length; ++i) {
            final float x = lastX + rnd.nextFloat() * 200 + lastW;
            final float h = rnd.nextFloat() * 70 + 5;
            final float w = rnd.nextFloat() * 35 + 10;
            group[i] = new RectangleCollider(x, BASE_LINE - h, w, h);

            lastX = x;
            lastW = w;
        }

        ball.x = 0;
        ball.y = 380;
        dx = 0;
        dy = 0;
        state = State.ZOMBIE;
    }

    @Override
    public void render(IGraphics g) {
        g.setClearColor(Color.black);
        g.clearGraphics();

        g.setColor(Color.white);

        g.drawLine(0, BASE_LINE, 500, BASE_LINE);

        g.translate(50 - ball.x, 0);

        ball.render(g);
        for (final Collider col : group) col.render(g);
    }

    @Override
    public boolean update(final float dt) {
        switch (state) {
            case ZOMBIE:
                if (scene.keyboard().isKeyPressed(KeyEvent.VK_X)) {
                    dy = -JUMP_SPEED;
                    state = State.JUMP_1;
                }
                break;
            case JUMP_1:
                if (scene.keyboard().isKeyPressed(KeyEvent.VK_X)) {
                    dy = -JUMP_SPEED * 1.5f;
                    state = State.JUMP_2;
                }
                break;
        }

        if (scene.keyboard().isKeyDown(KeyEvent.VK_Z)) {
            dy = JUMP_SPEED * 2.5f;
        }

        if (!penalty && scene.keyboard().isKeyDown(KeyEvent.VK_SHIFT)) {
            if ((dx -= X_SCROLL * 1.25f * dt) < -30) {
                dx = X_SCROLL;
                penalty = true;
            }
        }

        dx += X_SCROLL * dt;
        dy += Y_SCROLL * dt;

        ball.x += dx * dt;
        ball.y += dy * dt;

        if (ball.y > BASE_LINE - RADIUS) {
            ball.y = BASE_LINE - RADIUS;
            dy = 0;
            state = State.ZOMBIE;
        }

        if (ball.collidesWithAny(group)) {
            scene.switchToScene(0);
        }
        return true;
    }

    @Override
    public void resize(int w, int h) {
        // ignore
    }
}
