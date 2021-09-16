import { Ingredient } from "./ingredient";

export class Recipe {
    id?: number;
    name?: string;
    creationTime?: string;
    vegetarian?: boolean;
    servingCapacity?: number;
    ingredients?: Ingredient[];
    cookingInstructions?: string;
    lastModified?: Date;
}
