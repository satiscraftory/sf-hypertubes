package net.ugi.sf_hypertube;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.ugi.sf_hypertube.block.HypertubeBlock;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforged.mods.toml file
@Mod(SfHyperTube.MOD_ID)
public class SfHyperTube {
    public static final String MOD_ID = "sf_hypertube";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Use your current registration method. Here we also call our helper to register a BlockItem.
    public static final DeferredBlock<Block> HYPERTUBE_BLOCK = registerBlock("hypertube_block",
            () -> new HypertubeBlock(BlockBehaviour.Properties.of().noOcclusion()/*.setId(ResourceKey.create(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID,"hypertube_block")),ResourceLocation.fromNamespaceAndPath(MOD_ID,"hypertube_block")))*/));
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);
    //public static final DeferredItem<BlockItem> HYPERTUBE_ITEM = ITEMS.registerSimpleBlockItem("hypertube_block", HYPERTUBE_BLOCK);
    public static final DeferredHolder<EntityType<?>, EntityType<HypertubeEntity>> HYPERTUBE_ENTITY_TYPE =
            ENTITY_TYPES.register("hypertube_entity", () ->
                    EntityType.Builder.<HypertubeEntity>of(HypertubeEntity::new, MobCategory.MISC)
                            .sized(0.0F, 0.0F)   // Effectively invisible/collisionless
                            .build(ResourceLocation.fromNamespaceAndPath(MOD_ID, "hypertube_entity").toString())
            );


    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //ModItemProperties.addCustomItemProperties();

            EntityRenderers.register(HYPERTUBE_ENTITY_TYPE.get(), HypertubeEntityRenderer::new);
            //EntityRenderers.register(ModEntities.TOMAHAWK.get(), TomahawkProjectileRenderer::new);

            //EntityRenderers.register(ModEntities.CHAIR_ENTITY.get(), ChairRenderer::new);
        }
    }
    public static class HypertubeEntityRenderer extends EntityRenderer<HypertubeEntity> {

        public HypertubeEntityRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(HypertubeEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(SfHyperTube.MOD_ID, "textures/entity/hypertube.png");
        }

        @Override
        public void render(HypertubeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            // Optional: teken model of iets visueels
            super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        }
    }
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sf_hypertubes"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get());
                output.accept(HYPERTUBE_BLOCK.get());
            }).build());

    private final WeakHashMap<Player, HypertubeEntity> playerTubeEntity = new WeakHashMap<>();

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()/*.setId(ResourceKey.create(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, name)),ResourceLocation.fromNamespaceAndPath(MOD_ID, name)) )*/));
    }

    public SfHyperTube(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        // Use addListener for events from NeoForge
        NeoForge.EVENT_BUS.addListener(this::onPlayerTick);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
            event.accept(HYPERTUBE_BLOCK);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    // --- Modified Player Tick Event ---
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        Level world = player.level();
        BlockPos pos = player.blockPosition();

        List<BlockPos> positions = BlockPos.betweenClosedStream(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                .map(BlockPos::immutable)
                .toList();

        boolean foundTube = false;
        Direction tubeFacing = null;
        BlockPos tubeBlockPos = null;

        for (BlockPos checkPos : positions) {
            BlockState state = world.getBlockState(checkPos);
            if (state.is(HYPERTUBE_BLOCK.get())) {
                Direction facing = state.getValue(HypertubeBlock.FACING);
                Vec3 playerToBlock = Vec3.atCenterOf(checkPos).subtract(player.position());
                Vec3 facingVec = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
                double dot = playerToBlock.normalize().dot(facingVec);
                if (dot > 0.5) {
                    foundTube = true;
                    tubeFacing = facing;
                    tubeBlockPos = checkPos;
                    break;
                }
            }
        }

        if (foundTube && tubeBlockPos != null && tubeFacing != null) {
            // If the player is not riding a HypertubeEntity, spawn one.
            if (!player.isPassenger() || !(player.getVehicle() instanceof HypertubeEntity)) {
                HypertubeEntity tubeEntity = new HypertubeEntity(HYPERTUBE_ENTITY_TYPE.get(), world);
                // Set the entity inside the block; you can adjust the offset as needed.
                Vec3 insidePos = Vec3.atCenterOf(tubeBlockPos).add(0, 0.5, 0);
                tubeEntity.setPos(insidePos.x, insidePos.y, insidePos.z);
                world.addFreshEntity(tubeEntity);
                // Make the player ride the tube entity
                player.startRiding(tubeEntity, true);
                playerTubeEntity.put(player, tubeEntity);
            } else {
                // Update the tube entity's movement
                HypertubeEntity tubeEntity = (HypertubeEntity) player.getVehicle();
                Vec3 motion = new Vec3(tubeFacing.getStepX() * 0.5, tubeFacing.getStepY() * 0.5, tubeFacing.getStepZ() * 0.5);
                tubeEntity.setDeltaMovement(motion);
            }
        } else {
            // If the player is riding a tube entity but is no longer in the tube, remove the entity and dismount.
            if (player.isPassenger() && player.getVehicle() instanceof HypertubeEntity) {
                player.stopRiding();
                HypertubeEntity tubeEntity = (HypertubeEntity) player.getVehicle();
                if (tubeEntity != null) {
                    tubeEntity.remove(Entity.RemovalReason.DISCARDED);
                }
                playerTubeEntity.remove(player);
            }
        }
    }
    // --- End of Player Tick Event ---

    // --- HypertubeBlock Definition ---

    // --- End of HypertubeBlock ---

    // --- HypertubeEntity Definition ---
    // This is an invisible, rideable entity that moves with the hypertube.
    public static class HypertubeEntity extends Entity {

        public HypertubeEntity(EntityType<? extends HypertubeEntity> type, Level world) {
            super(type, world);
            this.noPhysics = true;
            this.setInvisible(true);
        }

        public boolean shouldRender(double x, double y, double z) {
            return false;
        }

        @Override
        protected void readAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        }

        @Override
        protected void addAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        }


        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {

        }

        @Override
        public void tick() {
            super.tick();
            // Optionally, here you can update entity-specific behavior if needed.
        }
    }
    // --- End of HypertubeEntity ---
}
