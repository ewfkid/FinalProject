package com.example.spacex.ui.article;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.spacex.R;
import com.example.spacex.data.ArticleRepositoryImpl;
import com.example.spacex.databinding.FragmentArticleBinding;
import com.example.spacex.domain.article.ArticleRepository;
import com.example.spacex.domain.article.CreateArticleUseCase;
import com.example.spacex.domain.article.DeleteArticleUseCase;
import com.example.spacex.domain.article.GetArticleListUseCase;
import com.example.spacex.domain.article.UpdateArticleUseCase;

import java.util.ArrayList;

public class ArticleFragment extends Fragment {

    private FragmentArticleBinding binding;
    private ArticleAdapter articleAdapter;
    private ArticleViewModel viewModel;

    public ArticleFragment() {
        super(R.layout.fragment_article);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentArticleBinding.bind(view);


        ArticleRepository articleRepository = new ArticleRepositoryImpl();


        CreateArticleUseCase createArticleUseCase = new CreateArticleUseCase(articleRepository);
        DeleteArticleUseCase deleteArticleUseCase = new DeleteArticleUseCase(articleRepository);
        UpdateArticleUseCase updateArticleUseCase = new UpdateArticleUseCase(articleRepository);
        GetArticleListUseCase getArticleListUseCase = new GetArticleListUseCase(articleRepository);


        ArticleViewModelFactory factory = new ArticleViewModelFactory(
                createArticleUseCase,
                deleteArticleUseCase,
                updateArticleUseCase,
                getArticleListUseCase
        );


        viewModel = new ViewModelProvider(this, factory).get(ArticleViewModel.class);


        setupRecyclerView();
        observeArticles();
        setupButtonListeners();
    }

    private void setupRecyclerView() {
        binding.articleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        articleAdapter = new ArticleAdapter(new ArrayList<>());
        binding.articleRecyclerView.setAdapter(articleAdapter);
    }

    private void observeArticles() {
        viewModel.getArticles().observe(getViewLifecycleOwner(), articles -> {
            articleAdapter.updateArticles(articles);
        });
    }

    private void setupButtonListeners() {
        binding.createArticleButton.setOnClickListener(v -> {
            String title = binding.articleTitleEditText.getText().toString();
            String content = binding.articleContentEditText.getText().toString();
            if (!title.isEmpty() && !content.isEmpty()) {
                addArticle(title, content);
            } else {
                showToast("Заполните все поля для добавления статьи");
            }
        });

        binding.deleteArticleButton.setOnClickListener(v -> {
            String idToDelete = binding.articleIdEditText.getText().toString();
            if (!idToDelete.isEmpty()) {
                deleteArticle(idToDelete);
            } else {
                showToast("Введите ID статьи для удаления");
            }
        });

        binding.updateArticleButton.setOnClickListener(v -> {
            String id = binding.articleIdEditText.getText().toString();
            String title = binding.articleTitleEditText.getText().toString();
            String content = binding.articleContentEditText.getText().toString();
            if (!id.isEmpty() && !title.isEmpty() && !content.isEmpty()) {
                updateArticle(id, title, content);
            } else {
                showToast("Заполните все поля для обновления статьи");
            }
        });

        binding.showAllArticlesButton.setOnClickListener(v -> showAllArticles());
    }

    private void addArticle(String title, String content) {
        viewModel.addArticle(title, content);
        showToast("Статья добавлена");
    }

    private void deleteArticle(String id) {
        viewModel.deleteArticle(id);
        showToast("Статья удалена");
    }

    private void updateArticle(String id, String title, String content) {
        viewModel.updateArticle(id, title, content);
        showToast("Статья обновлена");
    }

    private void showAllArticles() {
        viewModel.loadAllArticles();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
