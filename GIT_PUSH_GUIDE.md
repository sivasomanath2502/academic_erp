# Git Push Guide - Academic ERP Project

## 📋 Quick Start Guide

### Step 1: Check Current Status
```bash
cd D:\ESD\academic_erp
git status
```

### Step 2: Add All Files
```bash
# Add all files to staging
git add .

# Or add specific files/folders
git add frontend/
git add backend/
git add .gitignore
git add README.md
```

### Step 3: Commit Changes
```bash
# Make your first commit
git commit -m "Initial commit: Academic ERP project with frontend and backend"

# Or for subsequent commits
git commit -m "Description of your changes"
```

### Step 4: Set Up Remote Repository (First Time Only)

#### Option A: Create New Repository on GitHub
1. Go to [GitHub](https://github.com) and sign in
2. Click the **"+"** icon → **"New repository"**
3. Name it: `academic-erp` (or your preferred name)
4. **Don't** initialize with README, .gitignore, or license (we already have these)
5. Click **"Create repository"**

#### Option B: Use Existing Remote
If you already have a remote repository, skip to Step 5.

### Step 5: Add Remote Repository
```bash
# Replace YOUR_USERNAME and REPO_NAME with your actual values
git remote add origin https://github.com/YOUR_USERNAME/academic-erp.git

# Or if using SSH
git remote add origin git@github.com:YOUR_USERNAME/academic-erp.git

# Verify remote was added
git remote -v
```

### Step 6: Push to Remote
```bash
# For first push
git push -u origin main

# Or if your default branch is 'master'
git push -u origin master

# For subsequent pushes
git push
```

---

## 🔄 Complete Workflow Example

```bash
# 1. Navigate to project directory
cd D:\ESD\academic_erp

# 2. Check what files will be committed
git status

# 3. Add all files
git add .

# 4. Commit with descriptive message
git commit -m "Initial commit: Academic ERP - Student admission system with Google OAuth"

# 5. Add remote (first time only)
git remote add origin https://github.com/YOUR_USERNAME/academic-erp.git

# 6. Push to remote
git push -u origin main
```

---

## 📝 Common Git Commands

### Viewing Status
```bash
# See what files are changed
git status

# See detailed changes
git diff

# See commit history
git log --oneline
```

### Undoing Changes
```bash
# Unstage files (before commit)
git reset

# Undo changes to a file (before commit)
git checkout -- filename

# Amend last commit message
git commit --amend -m "New message"
```

### Branching (Optional)
```bash
# Create a new branch
git checkout -b feature-branch-name

# Switch branches
git checkout main

# Merge branch
git merge feature-branch-name
```

---

## ⚠️ Important Notes

### Files That Will Be Ignored
The following files/folders are in `.gitignore` and **will NOT** be pushed:
- `node_modules/` (frontend dependencies)
- `target/` (backend build output)
- `dist/` (frontend build output)
- `.env` files (environment variables)
- `uploads/` (user-uploaded photos)
- IDE configuration files (`.idea/`, `.vscode/`)

### Files That Will Be Pushed
- All source code (`.tsx`, `.ts`, `.java` files)
- Configuration files (`package.json`, `pom.xml`, `application.properties`)
- Documentation (`.md` files)
- Project structure

### Before Pushing - Checklist
- [ ] Review `git status` to see what will be committed
- [ ] Ensure sensitive data (passwords, API keys) are not in tracked files
- [ ] Check that `application.properties` doesn't contain secrets (or use environment variables)
- [ ] Verify `.gitignore` is working correctly
- [ ] Write a clear commit message

---

## 🔐 Security Reminders

### Never Commit:
- Passwords or API keys
- Database credentials
- Google OAuth client secrets (use environment variables)
- Personal information

### If You Accidentally Committed Secrets:
```bash
# Remove file from Git history (use with caution)
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch path/to/file" \
  --prune-empty --tag-name-filter cat -- --all

# Force push (warning: rewrites history)
git push origin --force --all
```

**Better approach**: Use environment variables or `.env` files (which are in `.gitignore`)

---

## 🚀 Step-by-Step: First Time Push

### 1. Initialize (if not already done)
```bash
cd D:\ESD\academic_erp
git init  # Only if not already initialized
```

### 2. Configure Git (if first time using Git)
```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 3. Stage Files
```bash
git add .
```

### 4. Commit
```bash
git commit -m "Initial commit: Academic ERP project"
```

### 5. Create GitHub Repository
- Go to GitHub.com
- Click "New repository"
- Name: `academic-erp`
- Don't initialize with README
- Click "Create"

### 6. Add Remote
```bash
git remote add origin https://github.com/YOUR_USERNAME/academic-erp.git
```

### 7. Push
```bash
git branch -M main  # Rename branch to 'main' if needed
git push -u origin main
```

---

## 📦 Subsequent Pushes

After the initial push, the workflow is simpler:

```bash
# 1. Make changes to your code

# 2. Check status
git status

# 3. Add changes
git add .

# 4. Commit
git commit -m "Description of changes"

# 5. Push
git push
```

---

## 🆘 Troubleshooting

### Error: "remote origin already exists"
```bash
# Remove existing remote
git remote remove origin

# Add new remote
git remote add origin https://github.com/YOUR_USERNAME/academic-erp.git
```

### Error: "failed to push some refs"
```bash
# Pull remote changes first
git pull origin main --rebase

# Then push
git push
```

### Error: "authentication failed"
- Use Personal Access Token instead of password
- Or set up SSH keys
- Or use GitHub Desktop app

### Check What Will Be Pushed
```bash
# See what files are tracked
git ls-files

# See what will be committed
git status
```

---

## 📚 Additional Resources

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Guides](https://guides.github.com/)
- [Git Cheat Sheet](https://education.github.com/git-cheat-sheet-education.pdf)

---

## ✅ Quick Reference

```bash
# Daily workflow
git status              # Check changes
git add .               # Stage all changes
git commit -m "message" # Commit changes
git push                # Push to remote

# First time setup
git remote add origin <repository-url>
git push -u origin main
```

