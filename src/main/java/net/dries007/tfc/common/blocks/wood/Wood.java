/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.level.material.MapColor;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.LoomBlockEntity;
import net.dries007.tfc.common.blockentities.SluiceBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCMaterials;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.common.items.BarrelBlockItem;
import net.dries007.tfc.common.items.ChestBlockItem;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryWood;
import net.dries007.tfc.world.feature.tree.TFCTreeGrower;
import org.jetbrains.annotations.Nullable;

/**
 * Default wood types used for block registration calls.
 *
 * @see RegistryWood for addon support, to use {@link BlockType}.
 */
public enum Wood implements RegistryWood
{
    ACACIA(false, MapColor.TERRACOTTA_ORANGE, MapColor.TERRACOTTA_LIGHT_GRAY, 7, 11),
    ASH(false, MapColor.TERRACOTTA_PINK, MapColor.TERRACOTTA_ORANGE, 8, 7),
    ASPEN(false, MapColor.TERRACOTTA_GREEN, MapColor.TERRACOTTA_WHITE, 7, 8),
    BIRCH(false, MapColor.COLOR_BROWN, MapColor.TERRACOTTA_WHITE, 7, 7),
    BLACKWOOD(false, MapColor.COLOR_BLACK, MapColor.COLOR_BROWN, 7, 8),
    CHESTNUT(false, MapColor.TERRACOTTA_RED, MapColor.COLOR_LIGHT_GREEN, 8, 7),
    DOUGLAS_FIR(true, MapColor.TERRACOTTA_YELLOW, MapColor.TERRACOTTA_BROWN, 7, 7),
    HICKORY(false, MapColor.TERRACOTTA_BROWN, MapColor.COLOR_GRAY, 7, 10),
    KAPOK(true, MapColor.COLOR_PINK, MapColor.COLOR_BROWN, 8, 7),
    MAPLE(false, MapColor.COLOR_ORANGE, MapColor.TERRACOTTA_GRAY, 8, 7),
    OAK(false, MapColor.WOOD, MapColor.COLOR_BROWN, 8, 10),
    PALM(true, MapColor.COLOR_ORANGE, MapColor.COLOR_BROWN, 7, 7),
    PINE(true, MapColor.TERRACOTTA_GRAY, MapColor.COLOR_GRAY, 7, 7),
    ROSEWOOD(false, MapColor.COLOR_RED, MapColor.TERRACOTTA_LIGHT_GRAY, 9, 8),
    SEQUOIA(true, MapColor.TERRACOTTA_RED, MapColor.TERRACOTTA_RED, 7, 18),
    SPRUCE(true, MapColor.TERRACOTTA_PINK, MapColor.TERRACOTTA_BLACK, 7, 7),
    SYCAMORE(false, MapColor.COLOR_YELLOW, MapColor.TERRACOTTA_LIGHT_GREEN, 7, 8),
    WHITE_CEDAR(true, MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_LIGHT_GRAY, 7, 7),
    WILLOW(false, MapColor.COLOR_GREEN, MapColor.TERRACOTTA_BROWN, 7, 11);

    public static final Wood[] VALUES = values();

    private final String serializedName;
    private final boolean conifer;
    private final MaterialColor woodColor;
    private final MaterialColor barkColor;
    private final TFCTreeGrower tree;
    private final int maxDecayDistance;
    private final int daysToGrow;

    Wood(boolean conifer, MaterialColor woodColor, MaterialColor barkColor, int maxDecayDistance, int daysToGrow)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.conifer = conifer;
        this.woodColor = woodColor;
        this.barkColor = barkColor;
        this.tree = new TFCTreeGrower(Helpers.identifier("tree/" + serializedName), Helpers.identifier("tree/" + serializedName + "_large"));
        this.maxDecayDistance = maxDecayDistance;
        this.daysToGrow = daysToGrow;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public boolean isConifer()
    {
        return conifer;
    }

    @Override
    public MaterialColor woodColor()
    {
        return woodColor;
    }

    @Override
    public MaterialColor barkColor()
    {
        return barkColor;
    }

    @Override
    public TFCTreeGrower tree()
    {
        return tree;
    }

    @Override
    public int maxDecayDistance()
    {
        return maxDecayDistance;
    }

    @Override
    public int daysToGrow()
    {
        return TFCConfig.SERVER.saplingGrowthDays.get(this).get();
    }

    public int defaultDaysToGrow()
    {
        return daysToGrow;
    }

    @Override
    public Supplier<Block> getBlock(BlockType type)
    {
        return TFCBlocks.WOODS.get(this).get(type);
    }

    public enum BlockType
    {
        LOG((self, wood) -> new LogBlock(ExtendedProperties.of(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.woodColor() : wood.barkColor()).strength(8f).sound(SoundType.WOOD).requiresCorrectToolForDrops().flammableLikeLogs(), wood.getBlock(self.stripped())), false),
        STRIPPED_LOG(wood -> new LogBlock(ExtendedProperties.of(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? wood.woodColor() : wood.barkColor()).strength(7.5f).sound(SoundType.WOOD).requiresCorrectToolForDrops().flammableLikeLogs(), null), false),
        WOOD((self, wood) -> new LogBlock(properties(wood).strength(8f).requiresCorrectToolForDrops().flammableLikeLogs(), wood.getBlock(self.stripped())), false),
        STRIPPED_WOOD(wood -> new LogBlock(properties(wood).strength(7.5f).requiresCorrectToolForDrops().flammableLikeLogs(), null), false),
        LEAVES((self, wood) -> TFCLeavesBlock.create(ExtendedProperties.of(Material.LEAVES, wood.woodColor()).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion().isViewBlocking(TFCBlocks::never).flammableLikeLeaves(), wood.maxDecayDistance(), wood.getBlock(self.fallenLeaves()), wood.getBlock(self.twig())), false),
        PLANKS(wood -> new ExtendedBlock(properties(wood).strength(1.5f, 3.0F).flammableLikePlanks()), false),
        SAPLING(wood -> new TFCSaplingBlock(wood.tree(), ExtendedProperties.of(Material.PLANT, MapColor.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS).flammableLikeLeaves().blockEntity(TFCBlockEntities.TICK_COUNTER), wood::daysToGrow), false),
        POTTED_SAPLING(wood -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, wood.getBlock(SAPLING), BlockBehaviour.Properties.of(Material.DECORATION).instabreak().noOcclusion()), false),
        BOOKSHELF(wood -> new BookshelfBlock(properties(wood).strength(2.0F, 3.0F).flammable(20, 30).enchantmentPower(s -> s.getValue(BookshelfBlock.BOOKS_STORED) / 2f).blockEntity(TFCBlockEntities.BOOKSHELF)), true),
        DOOR(wood -> new TFCDoorBlock(properties(wood).strength(3.0F).noOcclusion().flammableLikePlanks()), true),
        TRAPDOOR(wood -> new TFCTrapDoorBlock(properties(wood).strength(3.0F).noOcclusion().flammableLikePlanks()), true),
        FENCE(wood -> new TFCFenceBlock(properties(wood).strength(2.0F, 3.0F).flammableLikePlanks()), true),
        LOG_FENCE(wood -> new TFCFenceBlock(properties(wood).strength(2.0F, 3.0F).flammableLikeLogs()), true),
        FENCE_GATE(wood -> new TFCFenceGateBlock(properties(wood).strength(2.0F, 3.0F).flammableLikePlanks()), true),
        BUTTON(wood -> new TFCWoodButtonBlock(ExtendedProperties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD).flammableLikePlanks()), true),
        PRESSURE_PLATE(wood -> new TFCPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, properties(wood).noCollission().strength(0.5F).sound(SoundType.WOOD).flammableLikePlanks()), true),
        SLAB(wood -> new TFCSlabBlock(properties(wood).strength(1.5f, 3.0F).flammableLikePlanks()), true),
        STAIRS(wood -> new TFCStairBlock(() -> wood.getBlock(PLANKS).get().defaultBlockState(), properties(wood).strength(1.5f, 3.0F).sound(SoundType.WOOD).flammableLikePlanks()), true),
        TOOL_RACK(wood -> new ToolRackBlock(properties(wood).strength(2.0F).noOcclusion().blockEntity(TFCBlockEntities.TOOL_RACK)), true),
        TWIG(wood -> GroundcoverBlock.twig(ExtendedProperties.of(TFCMaterials.GROUNDCOVER).strength(0.05F, 0.0F).sound(SoundType.WOOD).noCollission().flammableLikeWool()), false),
        FALLEN_LEAVES(wood -> new FallenLeavesBlock(ExtendedProperties.of(TFCMaterials.GROUNDCOVER).noCollission().strength(0.05F, 0.0F).noOcclusion().sound(SoundType.CROP).flammableLikeWool()), false),
        VERTICAL_SUPPORT(wood -> new VerticalSupportBlock(properties(wood).strength(1.0F).noOcclusion().flammableLikeLogs()), false),
        HORIZONTAL_SUPPORT(wood -> new HorizontalSupportBlock(properties(wood).strength(1.0F).noOcclusion().flammableLikeLogs()), false),
        WORKBENCH(wood -> new TFCCraftingTableBlock(properties(wood).strength(2.5F).flammableLikeLogs()), true),
        TRAPPED_CHEST((self, wood) -> new TFCTrappedChestBlock(properties(wood).strength(2.5F).flammableLikeLogs().blockEntity(TFCBlockEntities.TRAPPED_CHEST).clientTicks(ChestBlockEntity::lidAnimateTick), wood.getSerializedName()), false, ChestBlockItem::new),
        CHEST((self, wood) -> new TFCChestBlock(properties(wood).strength(2.5F).flammableLikeLogs().blockEntity(TFCBlockEntities.CHEST).clientTicks(ChestBlockEntity::lidAnimateTick), wood.getSerializedName()), false, ChestBlockItem::new),
        LOOM(wood -> new TFCLoomBlock(properties(wood).strength(2.5F).noOcclusion().flammableLikePlanks().blockEntity(TFCBlockEntities.LOOM).ticks(LoomBlockEntity::tick), Helpers.identifier("block/wood/planks/" + wood.getSerializedName())), true),
        SLUICE(wood -> new SluiceBlock(properties(wood).strength(3F).noOcclusion().flammableLikeLogs().blockEntity(TFCBlockEntities.SLUICE).serverTicks(SluiceBlockEntity::serverTick)), false),
        SIGN(wood -> new TFCStandingSignBlock(properties(wood).noCollission().strength(1F).flammableLikePlanks().blockEntity(TFCBlockEntities.SIGN)), true),
        WALL_SIGN(wood -> new TFCWallSignBlock(properties(wood).noCollission().strength(1F).dropsLike(wood.getBlock(SIGN)).flammableLikePlanks().blockEntity(TFCBlockEntities.SIGN)), true),
        BARREL((self, wood) -> new BarrelBlock(properties(wood).strength(2.5f).flammableLikePlanks().noOcclusion().blockEntity(TFCBlockEntities.BARREL).serverTicks(BarrelBlockEntity::serverTick)), false, BarrelBlockItem::new),
        LECTERN(wood -> new TFCLecternBlock(properties(wood).noCollission().strength(2.5F).flammableLikePlanks().blockEntity(TFCBlockEntities.LECTERN)), false),
        SCRIBING_TABLE(wood -> new ScribingTableBlock(properties(wood).noOcclusion().strength(2.5F).flammable(20, 30)), false);

        private static ExtendedProperties properties(RegistryWood wood)
        {
            return ExtendedProperties.of(Material.WOOD, wood.woodColor()).sound(SoundType.WOOD);
        }

        private final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;
        private final boolean isPlanksVariant;
        private final BiFunction<BlockType, RegistryWood, Block> blockFactory;

        BlockType(Function<RegistryWood, Block> blockFactory, boolean isPlanksVariant)
        {
            this((self, wood) -> blockFactory.apply(wood), isPlanksVariant);
        }

        BlockType(BiFunction<BlockType, RegistryWood, Block> blockFactory, boolean isPlanksVariant)
        {
            this(blockFactory, isPlanksVariant, BlockItem::new);
        }

        BlockType(BiFunction<BlockType, RegistryWood, Block> blockFactory, boolean isPlanksVariant, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.blockFactory = blockFactory;
            this.isPlanksVariant = isPlanksVariant;
            this.blockItemFactory = blockItemFactory;
        }

        @Nullable
        public Function<Block, BlockItem> createBlockItem(Item.Properties properties)
        {
            return needsItem() ? block -> blockItemFactory.apply(block, properties) : null;
        }

        public String nameFor(RegistryWood wood)
        {
            return (isPlanksVariant ? "wood/planks/" + wood.getSerializedName() + "_" + name() : "wood/" + name() + "/" + wood.getSerializedName()).toLowerCase(Locale.ROOT);
        }

        public boolean needsItem()
        {
            return this != VERTICAL_SUPPORT && this != HORIZONTAL_SUPPORT && this != SIGN && this != WALL_SIGN && this != POTTED_SAPLING;
        }

        private BlockType stripped()
        {
            return switch (this)
                {
                    case LOG -> STRIPPED_LOG;
                    case WOOD -> STRIPPED_WOOD;
                    default ->
                        throw new IllegalStateException("Block type " + name() + " does not have a stripped variant");
                };
        }

        private BlockType twig()
        {
            return TWIG;
        }

        private BlockType fallenLeaves()
        {
            return FALLEN_LEAVES;
        }

        public Supplier<Block> create(RegistryWood wood)
        {
            return () -> blockFactory.apply(this, wood);
        }
    }
}
