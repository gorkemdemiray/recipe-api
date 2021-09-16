import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Recipe } from '../models/recipe';

const baseUrl = 'http://localhost:8080/api/recipes';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {

  httpHeaders = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  }

  constructor(private http: HttpClient) {
  }

  createRecipe(recipe: Recipe): Observable<Recipe> {
    return this.http.post<Recipe>(baseUrl, recipe, this.httpHeaders);
  }

  getRecipe(id: number): Observable<Recipe> {
    return this.http.get<Recipe>(`${baseUrl}/${id}`);
  }

  getAllRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(baseUrl);
  }

  updateRecipe(id:number, recipe: Recipe): Observable<Recipe> {
    return this.http.put<Recipe>(`${baseUrl}/${id}`, recipe, this.httpHeaders);
  }

  deleteRecipe(id: number): Observable<Recipe> {
    return this.http.delete<Recipe>(`${baseUrl}/${id}`);
  }
}
