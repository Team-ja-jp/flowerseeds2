package flowerseeds.fabulousflowers.server;

import flowerseeds.fabulousflowers.FlowerSeedsFabulousFlowers;
import flowerseeds.fabulousflowers.client.lang.EN_US;
import flowerseeds.fabulousflowers.client.models.FabulousFlowersBlockStateProvider;
import flowerseeds.fabulousflowers.client.models.FabulousFlowersItemModelProvider;

import flowerseeds.fabulousflowers.server.loot.FabulousFlowersLootTableProvider;
import flowerseeds.fabulousflowers.server.recipes.FabulousFlowersRecipeProvider;
import flowerseeds.fabulousflowers.server.tags.FabulousFlowersBlockTagProvider;
import flowerseeds.fabulousflowers.server.tags.FabulousFlowersItemTagProvider;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = FlowerSeedsFabulousFlowers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        String MODID = FlowerSeedsFabulousFlowers.MODID;

        generator.addProvider(true, new PackMetadataGenerator(packOutput)
                .add(OverlayMetadataSection.TYPE, new OverlayMetadataSection(List.of(
                        new OverlayMetadataSection.OverlayEntry(new InclusiveRange<>(0, Integer.MAX_VALUE), "pack_overlays_test"))))
                .add(PackMetadataSection.TYPE, new PackMetadataSection(
                        Component.translatable("flowerseeds.packmeta.description"),
                        DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                        Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));

        generator.addProvider(event.includeClient(), new EN_US(packOutput, MODID));
        generator.addProvider(event.includeServer(), new FabulousFlowersRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), FabulousFlowersLootTableProvider.create(packOutput));

        generator.addProvider(event.includeClient(), new FabulousFlowersBlockStateProvider(packOutput, MODID, existingFileHelper));
        generator.addProvider(event.includeClient(), new FabulousFlowersItemModelProvider(packOutput, MODID, existingFileHelper));

        FabulousFlowersBlockTagProvider blockTagGenerator = generator.addProvider(event.includeServer(),
               new FabulousFlowersBlockTagProvider(packOutput, lookupProvider, existingFileHelper, MODID));
        generator.addProvider(event.includeServer(), new FabulousFlowersItemTagProvider(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper, MODID));

    }
}