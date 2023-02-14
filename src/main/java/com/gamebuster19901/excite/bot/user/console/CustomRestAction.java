package com.gamebuster19901.excite.bot.user.console;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import com.gamebuster19901.excite.Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;

public class CustomRestAction<V> implements RestAction<V> {

	private final Callable<V> action;
	
	public CustomRestAction(Callable<V> action) {
		this.action = action;
	}

	@Override
	public JDA getJDA() {
		return Main.discordBot.jda;
	}

	@Override
	public RestAction<V> setCheck(BooleanSupplier checks) {
		return this;
	}

	@Override
	public void queue(Consumer<? super V> success, Consumer<? super Throwable> failure) {
		try {
			submit(false);
			success.accept(null);
		}
		catch(Throwable t) {
			failure.accept(null);
		}
	}

	@Override
	public V complete(boolean shouldQueue) throws RateLimitedException {
		submit(false);
		return null;
	}

	@Override
	public CompletableFuture<V> submit(boolean shouldQueue) {
		V val;
		try {
			val = action.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return CompletableFuture.completedFuture(val);
	}

}
