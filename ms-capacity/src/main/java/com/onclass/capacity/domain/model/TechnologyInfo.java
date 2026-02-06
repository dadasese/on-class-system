package com.onclass.capacity.domain.model;

public record TechnologyInfo(
        Long id,
        String name
) {
   public static TechnologyInfo of(Long id, String name){
       return new TechnologyInfo(id, name);
   }

}
