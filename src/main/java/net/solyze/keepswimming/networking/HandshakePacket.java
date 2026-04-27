package net.solyze.keepswimming.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.solyze.keepswimming.KeepSwimming;
import org.jspecify.annotations.NonNull;

public record HandshakePacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HandshakePacket> PACKET_ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(KeepSwimming.MOD_ID, "handshake"));

    public static final StreamCodec<FriendlyByteBuf, HandshakePacket> PACKET_CODEC =
            StreamCodec.unit(new HandshakePacket()).cast();

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}