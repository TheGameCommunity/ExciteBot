package com.gamebuster19901.excite.mixin;

import javax.mail.Address;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.gamebuster19901.excite.bot.mail.EmailAddress;

@Unique
@Mixin(value = Address.class, remap = false)
public abstract class AddressMixin implements EmailAddress {

}
