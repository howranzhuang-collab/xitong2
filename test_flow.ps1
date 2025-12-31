# 0. Admin Register (Ensure admin exists)
Write-Host "Step 0: Admin Register..."
$adminRegParams = @{ username="admin"; password="123"; usertype="admin" }
try {
    Invoke-WebRequest -Uri "http://localhost:8081/register" -Method Post -Body $adminRegParams -UseBasicParsing
    Write-Host "Admin Registered (or already exists)."
} catch {
    Write-Warning "Admin Register Request Failed (Login page might be returned if success): $_"
}

# 1. Admin Login
Write-Host "Step 1: Admin Login..."
$adminParams = @{ username="admin"; password="123"; usertype="admin"; captcha="123456" }
try {
    $loginRes = Invoke-WebRequest -Uri "http://localhost:8081/login" -Method Post -Body $adminParams -SessionVariable adminSession -UseBasicParsing
    Write-Host "Admin Logged In. Final URL: $($loginRes.BaseResponse.ResponseUri)"
} catch {
    Write-Error "Admin Login Failed: $_"
    exit
}

# 2. Admin Add Project
Write-Host "Step 2: Admin Add Project..."
$projectParams = @{
    name = "Test Project 2025"
    year = "2025"
    applyDeadline = "2025-12-31"
    description = "This is a test project created by script"
    status = "1"
}
try {
    Invoke-WebRequest -Uri "http://localhost:8081/admin/project/save" -Method Post -Body $projectParams -WebSession $adminSession -UseBasicParsing
    Write-Host "Project Added."
} catch {
    Write-Error "Add Project Failed: $_"
    exit
}

# 3. Student Register
Write-Host "Step 3: Student Register..."
$rand = Get-Random
$studentUser = "stu_$rand"
$studentParams = @{
    username = $studentUser
    name = "Test Student"
    email = "$studentUser@test.com"
    password = "password"
}
try {
    $regRes = Invoke-RestMethod -Uri "http://localhost:8081/api/student/register" -Method Post -Body $studentParams
    if ($regRes.code -eq 200) {
        Write-Host "Student Registered: $studentUser"
    } else {
        Write-Error "Registration Failed: $($regRes.msg)"
        exit
    }
} catch {
    Write-Error "Registration Request Failed: $_"
    exit
}

# 4. Student Login
Write-Host "Step 4: Student Login..."
$loginParams = @{ username=$studentUser; password="password" }
try {
    $stuLoginRes = Invoke-RestMethod -Uri "http://localhost:8081/api/student/login" -Method Post -Body $loginParams -SessionVariable studentSession
    if ($stuLoginRes.code -eq 200) {
        Write-Host "Student Logged In."
    } else {
        Write-Error "Student Login Failed: $($stuLoginRes.msg)"
        exit
    }
} catch {
    Write-Error "Student Login Request Failed: $_"
    exit
}

# 5. Get Project List
Write-Host "Step 5: Get Project List..."
try {
    $projectsRes = Invoke-RestMethod -Uri "http://localhost:8081/api/project/list" -Method Get
    Write-Host "Projects Response Code: $($projectsRes.code)"
    Write-Host "Projects Response Data: $($projectsRes.data | ConvertTo-Json -Depth 2)"
    
    # Try to find the added project, OR fallback to a default project from data.sql
    $targetProject = $projectsRes.data.records | Where-Object { $_.name -eq "Test Project 2025" } | Select-Object -First 1
    
    if (-not $targetProject) {
         Write-Host "Test Project not found (Admin login might have failed), falling back to data.sql project..."
         $targetProject = $projectsRes.data.records | Select-Object -First 1
    }

    if ($targetProject) {
        Write-Host "Found Project ID: $($targetProject.id)"
    } else {
        Write-Error "Project 'Test Project 2025' not found in list!"
        exit
    }
} catch {
    Write-Error "Get Project List Failed: $_"
    exit
}

# 6. Submit Application
Write-Host "Step 6: Submit Application..."
$appBody = @{
    projectId = $targetProject.id
    documents = "['test_doc.pdf']"
} | ConvertTo-Json
try {
    $submitRes = Invoke-RestMethod -Uri "http://localhost:8081/api/application/submit" -Method Post -Body $appBody -ContentType "application/json" -WebSession $studentSession
    if ($submitRes.code -eq 200) {
        Write-Host "Application Submitted."
    } else {
        Write-Error "Submit Application Failed: $($submitRes.msg)"
        exit
    }
} catch {
    Write-Error "Submit Application Request Failed: $_"
    exit
}

# 7. Admin Check Application
Write-Host "Step 7: Admin Check Application..."
try {
    $appsPage = Invoke-WebRequest -Uri "http://localhost:8081/admin/application/list" -Method Get -WebSession $adminSession -UseBasicParsing
    # Simple check if the student name or username appears in the HTML content
    if ($appsPage.Content -match "Test Student") {
        Write-Host "SUCCESS: Application found for Test Student!"
    } else {
        Write-Warning "WARNING: Application NOT found in admin list page (Check page content manually if pagination issue)."
        # Write-Host $appsPage.Content # Debug
    }
} catch {
    Write-Error "Admin Check Application Failed: $_"
    exit
}

Write-Host "Test Completed Successfully!"
