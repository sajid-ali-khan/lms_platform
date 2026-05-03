// src/components/instructor/LessonsTree.jsx
import { useState } from 'react';

const Icons = {
    chevronDown: (
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
            <path fillRule="evenodd" d="M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"/>
        </svg>
    ),
    chevronRight: (
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
            <path fillRule="evenodd" d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708z"/>
        </svg>
    ),
    folder: (
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
            <path d="M.54 3.87.5 3a2 2 0 0 1 2-2h3.672a2 2 0 0 1 1.414.586l.828.828A2 2 0 0 0 9.828 3H14.5A1.5 1.5 0 0 1 16 4.5v1.384a1.5 1.5 0 0 0-1.5-.884H1.5a1.5 1.5 0 0 0-1.46 1.87l.54 3zm-.54 8.13v-5.5a1.5 1.5 0 0 1 1.5-1.5h13a1.5 1.5 0 0 1 1.5 1.5v5.5a1.5 1.5 0 0 1-1.5 1.5h-13a1.5 1.5 0 0 1-1.5-1.5z"/>
        </svg>
    ),
    file: (
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
            <path d="M14 4.5V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h5.5L14 4.5zm-3 0A1.5 1.5 0 0 1 9.5 3V1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V4.5h-2z"/>
        </svg>
    ),
    video: (
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
            <path d="M0 12V4a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2zm6.79-6.907A.5.5 0 0 0 6 5.5v5a.5.5 0 0 0 .79.407l3.5-2.5a.5.5 0 0 0 0-.814l-3.5-2.5z"/>
        </svg>
    ),
    plus: (
        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16">
            <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
        </svg>
    ),
};

function TreeModule({ module, isExpanded, onToggle, activeLessonId, onSelectLesson, onAddLesson }) {
    const lessonCount = module.lessons?.length || 0;

    return (
        <div className="tree-module">
            <div 
                className={`tree-module-header ${isExpanded ? 'expanded' : ''}`}
                onClick={onToggle}
            >
                <span className="tree-toggle-icon">
                    {isExpanded ? Icons.chevronDown : Icons.chevronRight}
                </span>
                <span className="tree-folder-icon">{Icons.folder}</span>
                <span className="tree-module-title">{module.title || `Module ${module.sequenceOrder}`}</span>
                <span className="tree-module-count">{lessonCount} lesson{lessonCount !== 1 ? 's' : ''}</span>
            </div>
            
            {isExpanded && (
                <div className="tree-module-children">
                    {module.lessons?.map((lesson) => (
                        <button
                            key={lesson.id}
                            className={`tree-lesson ${activeLessonId === lesson.id ? 'active' : ''}`}
                            onClick={() => onSelectLesson(lesson.id)}
                        >
                            <span className="tree-lesson-icon">
                                {lesson.type === 'VIDEO' ? Icons.video : Icons.file}
                            </span>
                            <span className="tree-lesson-title">
                                {lesson.title || `Lesson ${lesson.sequenceOrder}`}
                            </span>
                        </button>
                    ))}
                    <button 
                        className="tree-add-btn"
                        onClick={(e) => { e.stopPropagation(); onAddLesson(module.id); }}
                    >
                        {Icons.plus} Add Lesson
                    </button>
                </div>
            )}
        </div>
    );
}

export default function LessonsTree({ modules, activeLessonId, onSelectLesson, onAddModule, onAddLesson }) {
    // Track which modules are expanded - default all expanded
    const [expandedModules, setExpandedModules] = useState(() => 
        modules.reduce((acc, m) => ({ ...acc, [m.id]: true }), {})
    );

    const toggleModule = (moduleId) => {
        setExpandedModules(prev => ({
            ...prev,
            [moduleId]: !prev[moduleId]
        }));
    };

    const expandAll = () => {
        setExpandedModules(modules.reduce((acc, m) => ({ ...acc, [m.id]: true }), {}));
    };

    const collapseAll = () => {
        setExpandedModules(modules.reduce((acc, m) => ({ ...acc, [m.id]: false }), {}));
    };

    return (
        <div className="tree-container">
            <div className="tree-header">
                <h3>Course Content</h3>
                <div className="tree-header-actions">
                    <button onClick={expandAll} className="tree-header-btn" title="Expand all">
                        Expand
                    </button>
                    <button onClick={collapseAll} className="tree-header-btn" title="Collapse all">
                        Collapse
                    </button>
                </div>
            </div>
            
            <div className="tree-body">
                {modules.map((module) => (
                    <TreeModule
                        key={module.id}
                        module={module}
                        isExpanded={expandedModules[module.id] ?? true}
                        onToggle={() => toggleModule(module.id)}
                        activeLessonId={activeLessonId}
                        onSelectLesson={onSelectLesson}
                        onAddLesson={onAddLesson}
                    />
                ))}
                
                <button className="tree-add-module-btn" onClick={onAddModule}>
                    {Icons.plus} Add Module
                </button>
            </div>
        </div>
    );
}
