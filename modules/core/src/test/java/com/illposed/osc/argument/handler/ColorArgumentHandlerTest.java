/*
 * Copyright (C) 2015, C. Ramakrishnan / Illposed Software.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.illposed.osc.argument.handler;

import com.illposed.osc.argument.ArgumentHandler;
import com.illposed.osc.OSCParseException;
import com.illposed.osc.OSCSerializeException;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorArgumentHandlerTest {

	private final Logger log = LoggerFactory.getLogger(ColorArgumentHandlerTest.class);

	private static final Color[] DEFAULT_COLORS = new Color[] {
		Color.BLACK,
		Color.BLUE,
		Color.CYAN,
		Color.DARK_GRAY,
		Color.GRAY,
		Color.GREEN,
		Color.LIGHT_GRAY,
		Color.MAGENTA,
		Color.ORANGE,
		Color.PINK,
		Color.RED,
		Color.WHITE,
		Color.YELLOW};

	static <T> T reparse(final ArgumentHandler<T> type, final int bufferSize, final T orig)
			throws OSCSerializeException, OSCParseException
	{
		// serialize
		final ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		type.serialize(buffer, orig);
		final ByteBuffer reparsableBuffer = (ByteBuffer) buffer.flip();

		// re-parse
		return type.parse(reparsableBuffer);
	}

	private static Color reparse(final Color orig)
			throws OSCSerializeException, OSCParseException
	{
		return reparse(ColorArgumentHandler.INSTANCE, 4, orig);
	}

	@Test
	public void testReconvertBytes() {

		final byte[] testBytes = new byte[] {
			-128,
			-1,
			0,
			1,
			127
		};
		final int[] testInts = new int[] {
			128,
			255,
			0,
			1,
			127
		};

		for (int tni = 0; tni < testBytes.length; tni++) {
			final byte origByte = testBytes[tni];
			final int origInt = testInts[tni];

			final int createdInt = ColorArgumentHandler.toUnsignedInt(origByte);
			Assert.assertEquals(origInt, createdInt);

			final byte createdByte = ColorArgumentHandler.toSignedByte(createdInt);
			Assert.assertEquals(origByte, createdByte);
		}
	}

	@Test
	public void testReparseDefaultColors() throws Exception {

		for (final Color orig : DEFAULT_COLORS) {
			Assert.assertEquals(orig, reparse(orig));
		}
	}

	/**
	 * Adds random alpha values between 0 and 255 to the default colors,
	 * and then tries to re-parse them.
	 * @throws Exception on re-parse failure
	 */
	@Test
	@SuppressWarnings("SpellCheckingInspection")
	public void testReparseDefaultColorsAlphaed() throws Exception {

		final long alphaRandomSeed = new Random().nextLong();
		log.debug("{}#testReparseDefaultColorsAlphaed:alphaRandomSeed: {}",
				ColorArgumentHandlerTest.class.getSimpleName(), alphaRandomSeed);
		final Random alphaRandom = new Random(alphaRandomSeed);
		final Color[] alphaedDefaultColors = Arrays.copyOf(DEFAULT_COLORS, DEFAULT_COLORS.length);
		for (int tci = 0; tci < alphaedDefaultColors.length; tci++) {
			final Color orig = alphaedDefaultColors[tci];
			final int alpha = alphaRandom.nextInt(256);
			final Color alphaed = new Color(orig.getRed(), orig.getGreen(), orig.getBlue(), alpha);
			alphaedDefaultColors[tci] = alphaed;
		}

		for (final Color origColor : alphaedDefaultColors) {
			Assert.assertEquals(origColor, reparse(origColor));
		}
	}
}
