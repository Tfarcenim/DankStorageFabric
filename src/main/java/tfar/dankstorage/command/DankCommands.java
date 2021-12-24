package tfar.dankstorage.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import tfar.dankstorage.DankStorage;

public class DankCommands {

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal(DankStorage.MODID)
                .then(Commands.literal("clear")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(3))
                        .then(Commands.literal("all")
                                .executes(DankCommands::clearAll))

                        .then(Commands.argument("id", IntegerArgumentType.integer(0))
                                .executes(DankCommands::clearID))
                )
        );
    }

    private static int clearAll(CommandContext<CommandSourceStack> context) {
        DankStorage.instance.data.clearAll();
        return 1;
    }

    private static int clearID(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context,"id");
         boolean success = DankStorage.instance.data.clearId(id);
        if (!success) {
            throw new CommandRuntimeException(new TranslatableComponent("dankstorage.command.clear_id.invalid_id"));
        }
        return 1;
    }


}
