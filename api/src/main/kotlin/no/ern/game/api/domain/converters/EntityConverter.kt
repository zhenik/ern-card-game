package no.ern.game.api.domain.converters

import no.ern.game.api.domain.dto.EntityDto
import no.ern.game.api.domain.model.Entity

class EntityConverter{

    companion object {

        fun transform(entity: Entity) : EntityDto{
            return EntityDto(
                    id=entity.id?.toString(),
                    field1 = entity.field1,
                    field2 = entity.field2
            )
        }

        fun transform(entities: Iterable<Entity>) : List<EntityDto>{
            return entities.map { transform(it) }
        }
    }
}