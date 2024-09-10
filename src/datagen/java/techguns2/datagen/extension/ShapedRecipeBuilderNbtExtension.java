package techguns2.datagen.extension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public class ShapedRecipeBuilderNbtExtension extends CraftingRecipeBuilder implements RecipeBuilder
{
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final CompoundTag nbt;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    @Nullable
    private String group;
    private boolean showNotification = true;

    public ShapedRecipeBuilderNbtExtension(RecipeCategory category, ItemLike result, int count, CompoundTag nbt)
    {
       this.category = category;
       this.result = result.asItem();
       this.count = count;
       this.nbt = nbt;
    }

    public ShapedRecipeBuilderNbtExtension define(Character key, TagKey<Item> value)
    {
        return this.define(key, Ingredient.of(value));
    }

    public ShapedRecipeBuilderNbtExtension define(Character key, ItemLike value)
    {
        return this.define(key, Ingredient.of(value));
    }

    public ShapedRecipeBuilderNbtExtension define(Character key, Ingredient value)
    {
        if (this.key.containsKey(key))
        {
            throw new IllegalArgumentException("Symbol '" + value + "' is already defined!");
        }
        else if (key == ' ')
        {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else
        {
            this.key.put(key, value);
            return this;
        }
    }

    public ShapedRecipeBuilderNbtExtension pattern(String pattern)
    {
        if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length())
        {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        else
        {
            this.rows.add(pattern);
            return this;
        }
    }

    public ShapedRecipeBuilderNbtExtension unlockedBy(String name, CriterionTriggerInstance criterion)
    {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    public ShapedRecipeBuilderNbtExtension group(@Nullable String group)
    {
        this.group = group;
        return this;
    }

    public ShapedRecipeBuilderNbtExtension showNotification(boolean showNotification)
    {
        this.showNotification = showNotification;
        return this;
    }

    public Item getResult()
    {
        return this.result;
    }

    public void save(Consumer<FinishedRecipe> p_126141_, ResourceLocation id)
    {
        this.ensureValid(id);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        p_126141_.accept(
                new ShapedRecipeBuilderNbtExtension.Result(id, this.result, this.count, this.nbt, this.group == null ? "" : this.group,
                        determineBookCategory(this.category), this.rows, this.key, this.advancement,
                        id.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.showNotification));
    }

    private void ensureValid(ResourceLocation id)
    {
        if (this.rows.isEmpty())
        {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else
        {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for (String s : this.rows)
            {
                for (int i = 0; i < s.length(); ++i)
                {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ')
                    {
                        throw new IllegalStateException(
                                "Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty())
            {
                throw new IllegalStateException(
                        "Ingredients are defined but not used in pattern for recipe " + id);
            }
            else if (this.rows.size() == 1 && this.rows.get(0).length() == 1)
            {
                throw new IllegalStateException("Shaped recipe " + id
                        + " only takes in a single item - should it be a shapeless recipe instead?");
            }
            else if (this.advancement.getCriteria().isEmpty())
            {
                throw new IllegalStateException("No way of obtaining recipe " + id);
            }
        }
    }

    public static class Result extends CraftingRecipeBuilder.CraftingResult
    {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final CompoundTag nbt;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final boolean showNotification;

        public Result(ResourceLocation id, Item result, int count, CompoundTag nbt, String group,
                CraftingBookCategory category, List<String> pattern, Map<Character, Ingredient> key,
                Advancement.Builder advancement, ResourceLocation advancementId, boolean showNotification)
        {
            super(category);
            this.id = id;
            this.result = result;
            this.count = count;
            this.nbt = nbt;
            this.group = group;
            this.pattern = pattern;
            this.key = key;
            this.advancement = advancement;
            this.advancementId = advancementId;
            this.showNotification = showNotification;
        }

        public void serializeRecipeData(JsonObject json)
        {
            super.serializeRecipeData(json);
            if (!this.group.isEmpty())
            {
                json.addProperty("group", this.group);
            }

            JsonArray jsonarray = new JsonArray();

            for (String s : this.pattern)
            {
                jsonarray.add(s);
            }

            json.add("pattern", jsonarray);
            JsonObject jsonobject = new JsonObject();

            for (Map.Entry<Character, Ingredient> entry : this.key.entrySet())
            {
                jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }

            json.add("key", jsonobject);
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
            if (this.count > 1)
            {
                jsonobject1.addProperty("count", this.count);
            }
            if (this.nbt != null && !this.nbt.isEmpty())
            {
                jsonobject1.addProperty("nbt", this.nbt.getAsString());
            }

            json.add("result", jsonobject1);
            json.addProperty("show_notification", this.showNotification);
        }

        public RecipeSerializer<?> getType()
        {
            return RecipeSerializer.SHAPED_RECIPE;
        }

        public ResourceLocation getId()
        {
            return this.id;
        }

        @Nullable
        public JsonObject serializeAdvancement()
        {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId()
        {
            return this.advancementId;
        }
    }
}
