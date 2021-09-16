import { Component, OnInit } from '@angular/core';
import { RecipeService } from '../../services/recipe.service';
import { Recipe } from '../../models/recipe';

@Component({
  selector: 'app-recipe-list',
  templateUrl: './recipe-list.component.html',
  styleUrls: ['./recipe-list.component.css']
})
export class RecipeListComponent implements OnInit {

  recipes?: Recipe[];

  constructor(private recipeService: RecipeService) { }

  ngOnInit(): void {
    this.getAllRecipes();
  }

  getAllRecipes(): void {
    this.recipeService.getAllRecipes()
      .subscribe(
        data => {
          this.recipes = data;
          console.log(data);
        },
        error => {
          console.log(error);
        });
  }

  deleteRecipe(id: any) {
    this.recipeService.deleteRecipe(id)
      .subscribe(
        data => {
          console.log(data);
          this.getAllRecipes();
        },
        error => console.log(error));
  }

}
