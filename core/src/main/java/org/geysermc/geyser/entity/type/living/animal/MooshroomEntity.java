/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.entity.type.living.animal;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.type.ObjectEntityMetadata;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import org.geysermc.geyser.entity.EntityDefinition;
import org.geysermc.geyser.inventory.GeyserItemStack;
import org.geysermc.geyser.inventory.item.StoredItemMappings;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.util.InteractionResult;
import org.geysermc.geyser.util.InteractiveTag;

import javax.annotation.Nonnull;
import java.util.UUID;

public class MooshroomEntity extends AnimalEntity {
    private boolean isBrown = false;

    public MooshroomEntity(GeyserSession session, int entityId, long geyserId, UUID uuid, EntityDefinition<?> definition, Vector3f position, Vector3f motion, float yaw, float pitch, float headYaw) {
        super(session, entityId, geyserId, uuid, definition, position, motion, yaw, pitch, headYaw);
    }

    public void setVariant(ObjectEntityMetadata<String> entityMetadata) {
        isBrown = entityMetadata.getValue().equals("brown");
        dirtyMetadata.put(EntityData.VARIANT, isBrown ? 1 : 0);
    }

    @Nonnull
    @Override
    protected InteractiveTag testMobInteraction(@Nonnull GeyserItemStack itemInHand) {
        StoredItemMappings storedItems = session.getItemMappings().getStoredItems();
        if (!isBaby()) {
            if (itemInHand.getJavaId() == storedItems.bowl()) {
                // Stew
                return InteractiveTag.MOOSHROOM_MILK_STEW;
            } else if (isAlive() && itemInHand.getJavaId() == storedItems.shears()) {
                // Shear items
                return InteractiveTag.MOOSHROOM_SHEAR;
            }
        }
        return super.testMobInteraction(itemInHand);
    }

    @Nonnull
    @Override
    protected InteractionResult mobInteract(@Nonnull GeyserItemStack itemInHand) {
        StoredItemMappings storedItems = session.getItemMappings().getStoredItems();
        boolean isBaby = isBaby();
        if (!isBaby && itemInHand.getJavaId() == storedItems.bowl()) {
            // Stew
            return InteractionResult.SUCCESS;
        } else if (!isBaby && isAlive() && itemInHand.getJavaId() == storedItems.shears()) {
            // Shear items
            return InteractionResult.SUCCESS;
        } else if (isBrown && session.getTagCache().isSmallFlower(itemInHand) && itemInHand.getMapping(session).isHasSuspiciousStewEffect()) {
            // ?
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(itemInHand);
    }
}
